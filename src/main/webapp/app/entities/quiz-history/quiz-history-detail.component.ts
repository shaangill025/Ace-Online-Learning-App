import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IQuizHistory } from 'app/shared/model/quiz-history.model';

@Component({
    selector: 'jhi-quiz-history-detail',
    templateUrl: './quiz-history-detail.component.html'
})
export class QuizHistoryDetailComponent implements OnInit {
    quizHistory: IQuizHistory;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ quizHistory }) => {
            this.quizHistory = quizHistory;
        });
    }

    previousState() {
        window.history.back();
    }
}
