import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';

import { ITimeCourseLog } from 'app/shared/model/time-course-log.model';
import { TimeCourseLogService } from './time-course-log.service';
import { ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer';
import { ICourseHistory } from 'app/shared/model/course-history.model';
import { CourseHistoryService } from 'app/entities/course-history';

@Component({
    selector: 'jhi-time-course-log-update',
    templateUrl: './time-course-log-update.component.html'
})
export class TimeCourseLogUpdateComponent implements OnInit {
    timeCourseLog: ITimeCourseLog;
    isSaving: boolean;

    customers: ICustomer[];

    coursehistories: ICourseHistory[];
    recorddate: string;

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected timeCourseLogService: TimeCourseLogService,
        protected customerService: CustomerService,
        protected courseHistoryService: CourseHistoryService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ timeCourseLog }) => {
            this.timeCourseLog = timeCourseLog;
            this.recorddate = this.timeCourseLog.recorddate != null ? this.timeCourseLog.recorddate.format(DATE_TIME_FORMAT) : null;
        });
        this.customerService.query().subscribe(
            (res: HttpResponse<ICustomer[]>) => {
                this.customers = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.courseHistoryService.query().subscribe(
            (res: HttpResponse<ICourseHistory[]>) => {
                this.coursehistories = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.timeCourseLog.recorddate = this.recorddate != null ? moment(this.recorddate, DATE_TIME_FORMAT) : null;
        if (this.timeCourseLog.id !== undefined) {
            this.subscribeToSaveResponse(this.timeCourseLogService.update(this.timeCourseLog));
        } else {
            this.subscribeToSaveResponse(this.timeCourseLogService.create(this.timeCourseLog));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<ITimeCourseLog>>) {
        result.subscribe((res: HttpResponse<ITimeCourseLog>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackCustomerById(index: number, item: ICustomer) {
        return item.id;
    }

    trackCourseHistoryById(index: number, item: ICourseHistory) {
        return item.id;
    }
}
