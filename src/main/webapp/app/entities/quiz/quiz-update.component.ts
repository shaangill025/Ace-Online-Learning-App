import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';

import { IQuiz } from 'app/shared/model/quiz.model';
import { QuizService } from './quiz.service';
import { ISection } from 'app/shared/model/section.model';
import { SectionService } from 'app/entities/section';

@Component({
    selector: 'jhi-quiz-update',
    templateUrl: './quiz-update.component.html'
})
export class QuizUpdateComponent implements OnInit {
    private _quiz: IQuiz;
    isSaving: boolean;

    newsections: ISection[];

    constructor(
        private jhiAlertService: JhiAlertService,
        private quizService: QuizService,
        private sectionService: SectionService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ quiz }) => {
            this.quiz = quiz;
        });
        this.sectionService.query({ filter: 'quiz-is-null' }).subscribe(
            (res: HttpResponse<ISection[]>) => {
                if (!this.quiz.newSection || !this.quiz.newSection.id) {
                    this.newsections = res.body;
                } else {
                    this.sectionService.find(this.quiz.newSection.id).subscribe(
                        (subRes: HttpResponse<ISection>) => {
                            this.newsections = [subRes.body].concat(res.body);
                        },
                        (subRes: HttpErrorResponse) => this.onError(subRes.message)
                    );
                }
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.quiz.id !== undefined) {
            this.subscribeToSaveResponse(this.quizService.update(this.quiz));
        } else {
            this.subscribeToSaveResponse(this.quizService.create(this.quiz));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IQuiz>>) {
        result.subscribe((res: HttpResponse<IQuiz>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

    trackSectionById(index: number, item: ISection) {
        return item.id;
    }
    get quiz() {
        return this._quiz;
    }

    set quiz(quiz: IQuiz) {
        this._quiz = quiz;
    }
}
