import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICourseCartBridge } from 'app/shared/model/course-cart-bridge.model';

@Component({
    selector: 'jhi-course-cart-bridge-detail',
    templateUrl: './course-cart-bridge-detail.component.html'
})
export class CourseCartBridgeDetailComponent implements OnInit {
    courseCartBridge: ICourseCartBridge;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ courseCartBridge }) => {
            this.courseCartBridge = courseCartBridge;
        });
    }

    previousState() {
        window.history.back();
    }
}
