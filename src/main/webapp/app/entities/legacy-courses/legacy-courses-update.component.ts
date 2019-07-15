import { Component, OnInit, ElementRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';

import { ILegacyCourses } from 'app/shared/model/legacy-courses.model';
import { LegacyCoursesService } from './legacy-courses.service';
import { ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer';
import { ICourse } from 'app/shared/model/course.model';
import { CourseService } from 'app/entities/course';

@Component({
    selector: 'jhi-legacy-courses-update',
    templateUrl: './legacy-courses-update.component.html'
})
export class LegacyCoursesUpdateComponent implements OnInit {
    legacyCourses: ILegacyCourses;
    isSaving: boolean;

    customers: ICustomer[];

    courses: ICourse[];

    constructor(
        protected dataUtils: JhiDataUtils,
        protected jhiAlertService: JhiAlertService,
        protected legacyCoursesService: LegacyCoursesService,
        protected customerService: CustomerService,
        protected courseService: CourseService,
        protected elementRef: ElementRef,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ legacyCourses }) => {
            this.legacyCourses = legacyCourses;
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

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }

    setFileData(event, entity, field, isImage) {
        this.dataUtils.setFileData(event, entity, field, isImage);
    }

    clearInputImage(field: string, fieldContentType: string, idInput: string) {
        this.dataUtils.clearInputImage(this.legacyCourses, this.elementRef, field, fieldContentType, idInput);
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.legacyCourses.id !== undefined) {
            this.subscribeToSaveResponse(this.legacyCoursesService.update(this.legacyCourses));
        } else {
            this.subscribeToSaveResponse(this.legacyCoursesService.create(this.legacyCourses));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<ILegacyCourses>>) {
        result.subscribe((res: HttpResponse<ILegacyCourses>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

    trackCourseById(index: number, item: ICourse) {
        return item.id;
    }
}
