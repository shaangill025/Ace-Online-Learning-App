import { Component, OnInit, OnDestroy, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiParseLinks, JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { Router } from '@angular/router';
import { ITopic } from 'app/shared/model/topic.model';
import { Principal } from 'app/core';
import { CourseService } from 'app/entities/course';
import { ITEMS_PER_PAGE } from 'app/shared';
import { TopicService } from './topic.service';
import { SERVER_API_URL } from 'app/app.constants';
import { NavbarService } from 'app/layouts/navbar/navbar.service';
import { Location } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import * as setInterval from 'core-js/library/fn/set-interval';
/*import adBlocker from 'just-detect-adblock';*/
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
    selector: 'jhi-topic',
    templateUrl: './topic.component.html'
})
export class TopicComponent implements OnInit, OnDestroy, AfterViewInit {
    topics: ITopic[];
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
    isCanada = false;
    isUSA = false;
    errorRegionLock = false;
    shouldnotbeinUsa = false;
    shouldnotbeinCanada = false;
    errorAdBlock = false;
    regionOutside = false;
    @ViewChild('adsBanner') ads: ElementRef;

    constructor(
        private topicService: TopicService,
        private jhiAlertService: JhiAlertService,
        private dataUtils: JhiDataUtils,
        private eventManager: JhiEventManager,
        private parseLinks: JhiParseLinks,
        private activatedRoute: ActivatedRoute,
        private principal: Principal,
        private router: Router,
        private courseService: CourseService,
        private navbarService: NavbarService,
        private location: Location,
        private http: HttpClient,
        private spinner: NgxSpinnerService
    ) {
        this.topics = [];
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

    public reloadPage() {
        location.reload();
    }

    ngAfterViewInit(): void {
        this.spinner.show();
        setTimeout(() => {
            // spinner ends after 5 seconds
            this.spinner.hide();
        }, 4000);
    }

    loadAll() {
        if (this.currentSearch) {
            this.topicService
                .search({
                    query: this.currentSearch,
                    page: this.page,
                    size: this.itemsPerPage,
                    sort: this.sort()
                })
                .subscribe(
                    (res: HttpResponse<ITopic[]>) => this.paginateTopics(res.body, res.headers),
                    (res: HttpErrorResponse) => this.onError(res.message)
                );
            return;
        }
        this.topicService
            .query({
                page: this.page,
                size: this.itemsPerPage,
                sort: this.sort()
            })
            .subscribe(
                (res: HttpResponse<ITopic[]>) => this.paginateTopics(res.body, res.headers),
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    reset() {
        this.page = 0;
        this.topics = [];
        this.loadAll();
    }

    loadPage(page) {
        this.page = page;
        this.loadAll();
    }

    clear() {
        this.topics = [];
        this.links = {
            last: 0
        };
        this.page = 0;
        this.predicate = 'id';
        this.reverse = true;
        this.currentSearch = '';
        this.loadAll();
    }

    searchUrl(topic: ITopic) {
        this.courseService.fromTopic(topic.name);
        this.router.navigateByUrl(SERVER_API_URL + '/course');
    }

    search(query) {
        if (!query) {
            return this.clear();
        }
        this.topics = [];
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
        this.isCanada = this.navbarService.getifCanada();
        this.isUSA = this.navbarService.getifUSA();
        this.checkRegionUrl();
        //unblock for ad block detect
        /*if (this.router.url.includes('/topic', 0)) {
            setInterval(() => {
                this.checkAdBlock();
            }, 5000);
        }*/
        this.loadAll();
        this.principal.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInTopics();
    }

    checkAdBlock() {
        if (this.navbarService.getAdBlocked()) {
            this.errorAdBlock = true;
        } else {
            this.errorAdBlock = false;
        }
    }

    checkRegionUrl() {
        if (this.isCanada && location.hostname.includes('aceaol.com')) {
            this.errorRegionLock = true;
            this.shouldnotbeinUsa = true;
        } else if (this.isUSA && location.hostname.includes('aceaol.ca')) {
            this.errorRegionLock = true;
            this.shouldnotbeinCanada = true;
        } else if (!this.isCanada && !this.isUSA) {
            this.errorRegionLock = true;
            this.regionOutside = true;
        }
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: ITopic) {
        return item.id;
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }

    registerChangeInTopics() {
        this.eventSubscriber = this.eventManager.subscribe('topicListModification', response => this.reset());
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    private paginateTopics(data: ITopic[], headers: HttpHeaders) {
        this.links = this.parseLinks.parse(headers.get('link'));
        this.totalItems = parseInt(headers.get('X-Total-Count'), 10);
        for (let i = 0; i < data.length; i++) {
            this.topics.push(data[i]);
        }
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
