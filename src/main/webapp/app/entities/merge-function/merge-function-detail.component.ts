import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMergeFunction } from 'app/shared/model/merge-function.model';

@Component({
    selector: 'jhi-merge-function-detail',
    templateUrl: './merge-function-detail.component.html'
})
export class MergeFunctionDetailComponent implements OnInit {
    mergeFunction: IMergeFunction;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ mergeFunction }) => {
            this.mergeFunction = mergeFunction;
        });
    }

    previousState() {
        window.history.back();
    }
}
