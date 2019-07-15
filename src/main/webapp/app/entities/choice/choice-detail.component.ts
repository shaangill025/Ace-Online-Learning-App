import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IChoice } from 'app/shared/model/choice.model';

@Component({
    selector: 'jhi-choice-detail',
    templateUrl: './choice-detail.component.html'
})
export class ChoiceDetailComponent implements OnInit {
    choice: IChoice;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ choice }) => {
            this.choice = choice;
        });
    }

    previousState() {
        window.history.back();
    }
}
