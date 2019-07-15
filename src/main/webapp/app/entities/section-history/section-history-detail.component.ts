import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISectionHistory } from 'app/shared/model/section-history.model';

@Component({
    selector: 'jhi-section-history-detail',
    templateUrl: './section-history-detail.component.html'
})
export class SectionHistoryDetailComponent implements OnInit {
    sectionHistory: ISectionHistory;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ sectionHistory }) => {
            this.sectionHistory = sectionHistory;
        });
    }

    previousState() {
        window.history.back();
    }
}
