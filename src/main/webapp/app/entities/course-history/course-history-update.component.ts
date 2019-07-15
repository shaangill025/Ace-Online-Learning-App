import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';

import { ICourseHistory } from 'app/shared/model/course-history.model';
import { CourseHistoryService } from './course-history.service';
import { ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer';
import { ICourse } from 'app/shared/model/course.model';
import { CourseService } from 'app/entities/course';

@Component({
    selector: 'jhi-course-history-update',
    templateUrl: './course-history-update.component.html'
})
export class CourseHistoryUpdateComponent implements OnInit {
    private _courseHistory: ICourseHistory;
    isSaving: boolean;

    customers: ICustomer[];

    courses: ICourse[];
    startdate: string;
    lastactivedate: string;

    constructor(
        private jhiAlertService: JhiAlertService,
        private courseHistoryService: CourseHistoryService,
        private customerService: CustomerService,
        private courseService: CourseService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ courseHistory }) => {
            this.courseHistory = courseHistory;
        });
        this.customerService.query().subscribe(
            (res: HttpResponse<ICustomer[]>) => {
                this.customers = res.body;
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
        this.courseHistory.startdate = moment(this.startdate, DATE_TIME_FORMAT);
        this.courseHistory.lastactivedate = moment(this.lastactivedate, DATE_TIME_FORMAT);
        if (this.courseHistory.id !== undefined) {
            this.subscribeToSaveResponse(this.courseHistoryService.update(this.courseHistory));
        } else {
            this.subscribeToSaveResponse(this.courseHistoryService.create(this.courseHistory));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ICourseHistory>>) {
        result.subscribe((res: HttpResponse<ICourseHistory>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

    trackCourseById(index: number, item: ICourse) {
        return item.id;
    }
    get courseHistory() {
        return this._courseHistory;
    }

    set courseHistory(courseHistory: ICourseHistory) {
        this._courseHistory = courseHistory;
        this.startdate = moment(courseHistory.startdate).format(DATE_TIME_FORMAT);
        this.lastactivedate = moment(courseHistory.lastactivedate).format(DATE_TIME_FORMAT);
    }
}
