import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICourseHistory } from 'app/shared/model/course-history.model';

@Component({
    selector: 'jhi-course-history-detail',
    templateUrl: './course-history-detail.component.html'
})
export class CourseHistoryDetailComponent implements OnInit {
    courseHistory: ICourseHistory;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ courseHistory }) => {
            this.courseHistory = courseHistory;
        });
    }

    previousState() {
        window.history.back();
    }
}
