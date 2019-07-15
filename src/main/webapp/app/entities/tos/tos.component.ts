import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { ITos } from 'app/shared/model/tos.model';
import { AccountService, Principal } from 'app/core';
import { TosService } from './tos.service';

@Component({
    selector: 'jhi-tos',
    templateUrl: './tos.component.html'
})
export class TosComponent implements OnInit, OnDestroy {
    tos: ITos[];
    currentAccount: any;
    eventSubscriber: Subscription;
    currentSearch: string;

    constructor(
        protected tosService: TosService,
        protected jhiAlertService: JhiAlertService,
        protected eventManager: JhiEventManager,
        protected activatedRoute: ActivatedRoute,
        protected accountService: AccountService,
        protected principal: Principal
    ) {
        this.currentSearch =
            this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search']
                ? this.activatedRoute.snapshot.params['search']
                : '';
    }

    loadAll() {
        if (this.currentSearch) {
            this.tosService
                .search({
                    query: this.currentSearch
                })
                .subscribe((res: HttpResponse<ITos[]>) => (this.tos = res.body), (res: HttpErrorResponse) => this.onError(res.message));
            return;
        }
        this.tosService.query().subscribe(
            (res: HttpResponse<ITos[]>) => {
                this.tos = res.body;
                this.currentSearch = '';
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    search(query) {
        if (!query) {
            return this.clear();
        }
        this.currentSearch = query;
        this.loadAll();
    }

    clear() {
        this.currentSearch = '';
        this.loadAll();
    }

    ngOnInit() {
        this.loadAll();
        this.principal.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInTos();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: ITos) {
        return item.id;
    }

    registerChangeInTos() {
        this.eventSubscriber = this.eventManager.subscribe('tosListModification', response => this.loadAll());
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
