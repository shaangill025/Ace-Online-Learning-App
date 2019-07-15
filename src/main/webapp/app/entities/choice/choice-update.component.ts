import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';

import { IChoice } from 'app/shared/model/choice.model';
import { ChoiceService } from './choice.service';
import { IQuestion } from 'app/shared/model/question.model';
import { QuestionService } from 'app/entities/question';

@Component({
    selector: 'jhi-choice-update',
    templateUrl: './choice-update.component.html'
})
export class ChoiceUpdateComponent implements OnInit {
    private _choice: IChoice;
    isSaving: boolean;

    questions: IQuestion[];

    constructor(
        private jhiAlertService: JhiAlertService,
        private choiceService: ChoiceService,
        private questionService: QuestionService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ choice }) => {
            this.choice = choice;
        });
        this.questionService.query().subscribe(
            (res: HttpResponse<IQuestion[]>) => {
                this.questions = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.choice.id !== undefined) {
            this.subscribeToSaveResponse(this.choiceService.update(this.choice));
        } else {
            this.subscribeToSaveResponse(this.choiceService.create(this.choice));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IChoice>>) {
        result.subscribe((res: HttpResponse<IChoice>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackQuestionById(index: number, item: IQuestion) {
        return item.id;
    }
    get choice() {
        return this._choice;
    }

    set choice(choice: IChoice) {
        this._choice = choice;
    }
}
