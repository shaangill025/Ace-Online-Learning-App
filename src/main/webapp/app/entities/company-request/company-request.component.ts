import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { JhiEventManager, JhiParseLinks, JhiAlertService } from 'ng-jhipster';

import { ICompanyRequest } from 'app/shared/model/company-request.model';
import { Principal } from 'app/core';

import { ITEMS_PER_PAGE } from 'app/shared';
import { CompanyRequestService } from './company-request.service';
import { CompanyService } from 'app/entities/company';
import { ICompany } from 'app/shared/model/company.model';

@Component({
    selector: 'jhi-company-request',
    templateUrl: './company-request.component.html'
})
export class CompanyRequestComponent implements OnInit, OnDestroy {
    companyRequests: ICompanyRequest[];
    currentAccount: any;
    eventSubscriber: Subscription;
    itemsPerPage: number;
    links: any;
    page: any;
    predicate: any;
    queryCount: any;
    reverse: any;
    totalItems: number;
    currentSearch: string;
    isSaving: boolean;
    tempCompany: ICompany = {};

    constructor(
        private companyRequestService: CompanyRequestService,
        private jhiAlertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private parseLinks: JhiParseLinks,
        private activatedRoute: ActivatedRoute,
        private principal: Principal,
        private companyService: CompanyService
    ) {
        this.companyRequests = [];
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.page = 0;
        this.links = {
            last: 0
        };
        this.predicate = 'id';
        this.reverse = true;
        this.currentSearch =
            this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search']
                ? this.activatedRoute.snapshot.params['search']
                : '';
    }

    loadAll() {
        if (this.currentSearch) {
            this.companyRequestService
                .search({
                    query: this.currentSearch,
                    page: this.page,
                    size: this.itemsPerPage,
                    sort: this.sort()
                })
                .subscribe(
                    (res: HttpResponse<ICompanyRequest[]>) => this.paginateCompanyRequests(res.body, res.headers),
                    (res: HttpErrorResponse) => this.onError(res.message)
                );
            return;
        }
        this.companyRequestService
            .query({
                page: this.page,
                size: this.itemsPerPage,
                sort: this.sort()
            })
            .subscribe(
                (res: HttpResponse<ICompanyRequest[]>) => this.paginateCompanyRequests(res.body, res.headers),
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    reset() {
        this.page = 0;
        this.companyRequests = [];
        this.loadAll();
    }

    loadPage(page) {
        this.page = page;
        this.loadAll();
    }

    clear() {
        this.companyRequests = [];
        this.links = {
            last: 0
        };
        this.page = 0;
        this.predicate = 'id';
        this.reverse = true;
        this.currentSearch = '';
        this.loadAll();
    }

    search(query) {
        if (!query) {
            return this.clear();
        }
        this.companyRequests = [];
        this.links = {
            last: 0
        };
        this.page = 0;
        this.predicate = '_score';
        this.reverse = false;
        this.currentSearch = query;
        this.loadAll();
    }

    ngOnInit() {
        this.loadAll();
        this.principal.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInCompanyRequests();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: ICompanyRequest) {
        return item.id;
    }

    registerChangeInCompanyRequests() {
        this.eventSubscriber = this.eventManager.subscribe('companyRequestListModification', response => this.reset());
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ICompany>>) {
        result.subscribe((res: HttpResponse<ICompany>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
    }

    private onSaveError() {
        this.isSaving = false;
    }

    public addToDatabase(request: ICompanyRequest) {
        console.log('add to database - ' + request.name);
        this.tempCompany.country = request.country;
        this.tempCompany.city = request.city;
        this.tempCompany.description = request.description;
        this.tempCompany.licenceCycle = request.licenceCycle;
        this.tempCompany.name = request.name;
        this.tempCompany.phone = request.phone;
        this.tempCompany.postalCode = request.postalCode;
        this.tempCompany.notes = 'added via company request form';
        this.tempCompany.streetAddress = request.streetAddress;
        this.tempCompany.stateProvince = request.stateProvince;
        this.tempCompany.url = request.url;
        this.tempCompany.show = true;
        this.subscribeToSaveResponse(this.companyService.create(this.tempCompany));
    }

    private paginateCompanyRequests(data: ICompanyRequest[], headers: HttpHeaders) {
        this.links = this.parseLinks.parse(headers.get('link'));
        this.totalItems = parseInt(headers.get('X-Total-Count'), 10);
        for (let i = 0; i < data.length; i++) {
            this.companyRequests.push(data[i]);
        }
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
