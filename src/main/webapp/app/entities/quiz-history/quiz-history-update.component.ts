import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';

import { IQuizHistory } from 'app/shared/model/quiz-history.model';
import { QuizHistoryService } from './quiz-history.service';
import { ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer';
import { IQuiz } from 'app/shared/model/quiz.model';
import { QuizService } from 'app/entities/quiz';

@Component({
    selector: 'jhi-quiz-history-update',
    templateUrl: './quiz-history-update.component.html'
})
export class QuizHistoryUpdateComponent implements OnInit {
    private _quizHistory: IQuizHistory;
    isSaving: boolean;

    customers: ICustomer[];

    quizzes: IQuiz[];
    start: string;

    constructor(
        private jhiAlertService: JhiAlertService,
        private quizHistoryService: QuizHistoryService,
        private customerService: CustomerService,
        private quizService: QuizService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ quizHistory }) => {
            this.quizHistory = quizHistory;
        });
        this.customerService.query().subscribe(
            (res: HttpResponse<ICustomer[]>) => {
                this.customers = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.quizService.query().subscribe(
            (res: HttpResponse<IQuiz[]>) => {
                this.quizzes = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.quizHistory.start = moment(this.start, DATE_TIME_FORMAT);
        if (this.quizHistory.id !== undefined) {
            this.subscribeToSaveResponse(this.quizHistoryService.update(this.quizHistory));
        } else {
            this.subscribeToSaveResponse(this.quizHistoryService.create(this.quizHistory));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IQuizHistory>>) {
        result.subscribe((res: HttpResponse<IQuizHistory>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

    trackQuizById(index: number, item: IQuiz) {
        return item.id;
    }
    get quizHistory() {
        return this._quizHistory;
    }

    set quizHistory(quizHistory: IQuizHistory) {
        this._quizHistory = quizHistory;
        this.start = moment(quizHistory.start).format(DATE_TIME_FORMAT);
    }
}
