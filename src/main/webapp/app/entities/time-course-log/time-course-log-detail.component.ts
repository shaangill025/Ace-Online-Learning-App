import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITimeCourseLog } from 'app/shared/model/time-course-log.model';

@Component({
    selector: 'jhi-time-course-log-detail',
    templateUrl: './time-course-log-detail.component.html'
})
export class TimeCourseLogDetailComponent implements OnInit {
    timeCourseLog: ITimeCourseLog;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ timeCourseLog }) => {
            this.timeCourseLog = timeCourseLog;
        });
    }

    previousState() {
        window.history.back();
    }
}
