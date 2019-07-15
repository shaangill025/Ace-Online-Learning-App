import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';

import { IServicelist } from 'app/shared/model/servicelist.model';
import { ServicelistService } from './servicelist.service';
import { ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer';

@Component({
    selector: 'jhi-servicelist-update',
    templateUrl: './servicelist-update.component.html'
})
export class ServicelistUpdateComponent implements OnInit {
    private _servicelist: IServicelist;
    isSaving: boolean;

    customers: ICustomer[];

    constructor(
        private jhiAlertService: JhiAlertService,
        private servicelistService: ServicelistService,
        private customerService: CustomerService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ servicelist }) => {
            this.servicelist = servicelist;
        });
        this.customerService.query({ filter: 'servicelist-is-null' }).subscribe(
            (res: HttpResponse<ICustomer[]>) => {
                if (!this.servicelist.customer || !this.servicelist.customer.id) {
                    this.customers = res.body;
                } else {
                    this.customerService.find(this.servicelist.customer.id).subscribe(
                        (subRes: HttpResponse<ICustomer>) => {
                            this.customers = [subRes.body].concat(res.body);
                        },
                        (subRes: HttpErrorResponse) => this.onError(subRes.message)
                    );
                }
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.servicelist.id !== undefined) {
            this.subscribeToSaveResponse(this.servicelistService.update(this.servicelist));
        } else {
            this.subscribeToSaveResponse(this.servicelistService.create(this.servicelist));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IServicelist>>) {
        result.subscribe((res: HttpResponse<IServicelist>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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
    get servicelist() {
        return this._servicelist;
    }

    set servicelist(servicelist: IServicelist) {
        this._servicelist = servicelist;
    }
}
