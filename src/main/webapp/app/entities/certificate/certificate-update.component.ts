import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';

import { ICertificate } from 'app/shared/model/certificate.model';
import { CertificateService } from './certificate.service';
import { ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer';
import { ICourseHistory } from 'app/shared/model/course-history.model';
import { CourseHistoryService } from 'app/entities/course-history';

@Component({
    selector: 'jhi-certificate-update',
    templateUrl: './certificate-update.component.html'
})
export class CertificateUpdateComponent implements OnInit {
    certificate: ICertificate;
    isSaving: boolean;

    customers: ICustomer[];

    coursehistories: ICourseHistory[];
    timestamp: string;

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected certificateService: CertificateService,
        protected customerService: CustomerService,
        protected courseHistoryService: CourseHistoryService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ certificate }) => {
            this.certificate = certificate;
            this.timestamp = this.certificate.timestamp != null ? this.certificate.timestamp.format(DATE_TIME_FORMAT) : null;
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
        this.certificate.timestamp = this.timestamp != null ? moment(this.timestamp, DATE_TIME_FORMAT) : null;
        if (this.certificate.id !== undefined) {
            this.subscribeToSaveResponse(this.certificateService.update(this.certificate));
        } else {
            this.subscribeToSaveResponse(this.certificateService.create(this.certificate));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<ICertificate>>) {
        result.subscribe((res: HttpResponse<ICertificate>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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
