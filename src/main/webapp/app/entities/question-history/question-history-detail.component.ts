import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IQuestionHistory } from 'app/shared/model/question-history.model';

@Component({
    selector: 'jhi-question-history-detail',
    templateUrl: './question-history-detail.component.html'
})
export class QuestionHistoryDetailComponent implements OnInit {
    questionHistory: IQuestionHistory;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ questionHistory }) => {
            this.questionHistory = questionHistory;
        });
    }

    previousState() {
        window.history.back();
    }
}
