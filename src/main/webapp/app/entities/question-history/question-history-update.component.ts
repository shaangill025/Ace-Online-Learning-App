import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';

import { IQuestionHistory } from 'app/shared/model/question-history.model';
import { QuestionHistoryService } from './question-history.service';
import { ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer';
import { IQuestion } from 'app/shared/model/question.model';
import { QuestionService } from 'app/entities/question';

@Component({
    selector: 'jhi-question-history-update',
    templateUrl: './question-history-update.component.html'
})
export class QuestionHistoryUpdateComponent implements OnInit {
    private _questionHistory: IQuestionHistory;
    isSaving: boolean;

    customers: ICustomer[];

    questions: IQuestion[];
    timestamp: string;

    constructor(
        private jhiAlertService: JhiAlertService,
        private questionHistoryService: QuestionHistoryService,
        private customerService: CustomerService,
        private questionService: QuestionService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ questionHistory }) => {
            this.questionHistory = questionHistory;
        });
        this.customerService.query().subscribe(
            (res: HttpResponse<ICustomer[]>) => {
                this.customers = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
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
        this.questionHistory.timestamp = moment(this.timestamp, DATE_TIME_FORMAT);
        if (this.questionHistory.id !== undefined) {
            this.subscribeToSaveResponse(this.questionHistoryService.update(this.questionHistory));
        } else {
            this.subscribeToSaveResponse(this.questionHistoryService.create(this.questionHistory));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IQuestionHistory>>) {
        result.subscribe((res: HttpResponse<IQuestionHistory>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

    trackCustomerById(index: number, item: ICustomer) {
        return item.id;
    }

    trackQuestionById(index: number, item: IQuestion) {
        return item.id;
    }
    get questionHistory() {
        return this._questionHistory;
    }

    set questionHistory(questionHistory: IQuestionHistory) {
        this._questionHistory = questionHistory;
        this.timestamp = moment(questionHistory.timestamp).format(DATE_TIME_FORMAT);
    }
}
