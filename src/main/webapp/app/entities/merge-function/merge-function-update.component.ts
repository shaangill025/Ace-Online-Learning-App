import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { JhiAlertService } from 'ng-jhipster';

import { IMergeFunction } from 'app/shared/model/merge-function.model';
import { MergeFunctionService } from './merge-function.service';
import { ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer';
import {SERVER_API_URL} from "app/app.constants";
import {street1} from "aws-sdk/clients/importexport";

@Component({
    selector: 'jhi-merge-function-update',
    templateUrl: './merge-function-update.component.html'
})
export class MergeFunctionUpdateComponent implements OnInit {
    mergeFunction: IMergeFunction;
    isSaving: boolean;

    customers: ICustomer[];
    datetimeDp: any;

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected mergeFunctionService: MergeFunctionService,
        protected customerService: CustomerService,
        protected activatedRoute: ActivatedRoute,
        protected router: Router
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ mergeFunction }) => {
            this.mergeFunction = mergeFunction;
        });
        this.customerService.query().subscribe(
            (res: HttpResponse<ICustomer[]>) => {
                this.customers = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.mergeFunction.datetime = moment();
        if (this.mergeFunction.id !== undefined) {
            this.subscribeToSaveResponse(this.mergeFunctionService.update(this.mergeFunction));
        } else {
            this.subscribeToSaveResponse(this.mergeFunctionService.create(this.mergeFunction));
            this.customerService.merge(this.mergeFunction.tobeRemoved.id, this.mergeFunction.replacement.id).subscribe(str => {
                console.log('After merge log - ' + str);
            });
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IMergeFunction>>) {
        result.subscribe((res: HttpResponse<IMergeFunction>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.router.navigateByUrl(SERVER_API_URL+ 'customer');
        // this.previousState();
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
}
