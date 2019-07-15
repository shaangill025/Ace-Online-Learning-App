import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';

import { ISectionHistory } from 'app/shared/model/section-history.model';
import { SectionHistoryService } from './section-history.service';
import { ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer';
import { ISection } from 'app/shared/model/section.model';
import { SectionService } from 'app/entities/section';

@Component({
    selector: 'jhi-section-history-update',
    templateUrl: './section-history-update.component.html'
})
export class SectionHistoryUpdateComponent implements OnInit {
    private _sectionHistory: ISectionHistory;
    isSaving: boolean;
    customers: ICustomer[];
    sections: ISection[];
    startdate: string;
    lastactivedate: string;

    constructor(
        private jhiAlertService: JhiAlertService,
        private sectionHistoryService: SectionHistoryService,
        private customerService: CustomerService,
        private sectionService: SectionService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ sectionHistory }) => {
            this.sectionHistory = sectionHistory;
        });
        this.customerService.query().subscribe(
            (res: HttpResponse<ICustomer[]>) => {
                this.customers = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.sectionService.query().subscribe(
            (res: HttpResponse<ISection[]>) => {
                this.sections = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.sectionHistory.startdate = moment(this.startdate, DATE_TIME_FORMAT);
        this.sectionHistory.lastactivedate = moment(this.lastactivedate, DATE_TIME_FORMAT);
        if (this.sectionHistory.id !== undefined) {
            this.subscribeToSaveResponse(this.sectionHistoryService.update(this.sectionHistory));
        } else {
            this.subscribeToSaveResponse(this.sectionHistoryService.create(this.sectionHistory));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ISectionHistory>>) {
        result.subscribe((res: HttpResponse<ISectionHistory>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

    trackSectionById(index: number, item: ISection) {
        return item.id;
    }
    get sectionHistory() {
        return this._sectionHistory;
    }

    set sectionHistory(sectionHistory: ISectionHistory) {
        this._sectionHistory = sectionHistory;
        this.startdate = moment(sectionHistory.startdate).format(DATE_TIME_FORMAT);
        this.lastactivedate = moment(sectionHistory.lastactivedate).format(DATE_TIME_FORMAT);
    }
}
