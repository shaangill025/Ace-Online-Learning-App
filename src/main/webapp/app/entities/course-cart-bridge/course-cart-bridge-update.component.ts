import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';

import { ICourseCartBridge } from 'app/shared/model/course-cart-bridge.model';
import { CourseCartBridgeService } from './course-cart-bridge.service';
import { ICart } from 'app/shared/model/cart.model';
import { CartService } from 'app/entities/cart';
import { ICourse } from 'app/shared/model/course.model';
import { CourseService } from 'app/entities/course';

@Component({
    selector: 'jhi-course-cart-bridge-update',
    templateUrl: './course-cart-bridge-update.component.html'
})
export class CourseCartBridgeUpdateComponent implements OnInit {
    private _courseCartBridge: ICourseCartBridge;
    isSaving: boolean;

    carts: ICart[];

    courses: ICourse[];
    timestamp: string;

    constructor(
        private jhiAlertService: JhiAlertService,
        private courseCartBridgeService: CourseCartBridgeService,
        private cartService: CartService,
        private courseService: CourseService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ courseCartBridge }) => {
            this.courseCartBridge = courseCartBridge;
        });
        this.cartService.query().subscribe(
            (res: HttpResponse<ICart[]>) => {
                this.carts = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.courseService.query().subscribe(
            (res: HttpResponse<ICourse[]>) => {
                this.courses = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.courseCartBridge.timestamp = moment(this.timestamp, DATE_TIME_FORMAT);
        if (this.courseCartBridge.id !== undefined) {
            this.subscribeToSaveResponse(this.courseCartBridgeService.update(this.courseCartBridge));
        } else {
            this.subscribeToSaveResponse(this.courseCartBridgeService.create(this.courseCartBridge));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ICourseCartBridge>>) {
        result.subscribe((res: HttpResponse<ICourseCartBridge>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

    trackCartById(index: number, item: ICart) {
        return item.id;
    }

    trackCourseById(index: number, item: ICourse) {
        return item.id;
    }
    get courseCartBridge() {
        return this._courseCartBridge;
    }

    set courseCartBridge(courseCartBridge: ICourseCartBridge) {
        this._courseCartBridge = courseCartBridge;
        this.timestamp = moment(courseCartBridge.timestamp).format(DATE_TIME_FORMAT);
    }
}
