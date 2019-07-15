import { Component, OnInit, OnDestroy, ChangeDetectorRef, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager, JhiParseLinks, JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { ICourse } from 'app/shared/model/course.model';
import { Principal, UserService, IUser, Account } from 'app/core';
import { Location } from '@angular/common';
import { createRequestOption, DATE_TIME_FORMAT, ITEMS_PER_PAGE } from 'app/shared';
import { CourseService } from './course.service';
import { ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer';
import { CartService } from 'app/entities/cart';
import { courseCartBridgePopupRoute, CourseCartBridgeService } from 'app/entities/course-cart-bridge';
import { ICart } from 'app/shared/model/cart.model';
import { CourseCartBridge, ICourseCartBridge } from 'app/shared/model/course-cart-bridge.model';
import * as moment from 'moment';
import { JhiTrackerService } from 'app/core';
import { IamUserArn } from 'aws-sdk/clients/codedeploy';
import { __values } from 'tslib';
import { SERVER_API_URL } from 'app/app.constants';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { updateSourceFile } from '@angular/compiler-cli/src/transformers/node_emitter';
import { map } from 'rxjs/operators';
import { NavbarService } from 'app/layouts/navbar/navbar.service';
import * as setInterval from 'core-js/library/fn/set-interval';
import adBlocker from 'just-detect-adblock';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
    selector: 'jhi-course',
    templateUrl: './course.component.html'
})
export class CourseComponent implements OnInit, OnDestroy, AfterViewInit {
    courses: ICourse[];
    currentAccount: Account;
    eventSubscriber: Subscription;
    itemsPerPage: number;
    links: any;
    query = 'Search';
    isCanada = false;
    isUsa = false;
    isCanadaUrl = false;
    isUsaUrl = false;
    errorRegionLock = false;
    shouldnotbeinCanada = false;
    shouldnotbeinUsa = false;
    page: any;
    predicate: any;
    queryCount: any;
    reverse: any;
    totalItems: number;
    currentSearch: string;
    customer: ICustomer;
    cart: ICart;
    bridgeCart: ICourseCartBridge[];
    newBridgeCart: ICourseCartBridge;
    timestamp: string;
    user: IUser;
    userID = 0;
    message: string;
    isSaving: boolean;
    flagTemp = 0;
    currentSearchTemp: string;
    errorAdBlock = false;
    regionOutside = false;
    @ViewChild('adsBanner') ads: ElementRef;

    constructor(
        private courseService: CourseService,
        private customerService: CustomerService,
        private cartService: CartService,
        private courseCartService: CourseCartBridgeService,
        private jhiAlertService: JhiAlertService,
        private dataUtils: JhiDataUtils,
        private eventManager: JhiEventManager,
        private parseLinks: JhiParseLinks,
        private activatedRoute: ActivatedRoute,
        private userService: UserService,
        private trackService: JhiTrackerService,
        private principal: Principal,
        private navbarService: NavbarService,
        private http: HttpClient,
        private router: Router,
        private location: Location,
        private spinner: NgxSpinnerService
    ) {
        this.courses = [];
        this.message = '';
        this.bridgeCart = [];
        this.cart = {};
        this.customer = {};
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
            this.courseService
                .search({
                    query: this.currentSearch,
                    page: this.page,
                    size: this.itemsPerPage,
                    sort: this.sort()
                })
                .subscribe(
                    (res: HttpResponse<ICourse[]>) => this.paginateCourses(res.body, res.headers),
                    (res: HttpErrorResponse) => this.onError(res.message)
                );
            return;
        }
        /*this.courseService
            .query({
                page: this.page,
                size: this.itemsPerPage,
                sort: this.sort()
            })
            .subscribe(
                (res: HttpResponse<ICourse[]>) => this.paginateCourses(res.body, res.headers),
                (res: HttpErrorResponse) => this.onError(res.message)
            );*/
        // this.isUsa = this.navbarService.getifUSA();
        // this.isCanada = this.navbarService.getifCanada();
        this.isUsaUrl = false;
        this.isCanadaUrl = false;
        if ((location.hostname + this.router.url).includes('aceaol.com')) {
            this.isUsaUrl = true;
        } else if ((location.hostname + this.router.url).includes('aceaol.ca')) {
            this.isCanadaUrl = true;
        } else {
            this.isCanadaUrl = true;
        }

        if (this.isUsaUrl) {
            this.courseService.bycountry('USA').subscribe(data => {
                this.courses = data;
            });
        } else if (this.isCanadaUrl) {
            this.courseService.bycountry('Canada').subscribe(data => {
                this.courses = data;
            });
        }
        return;
    }

    reset() {
        this.page = 0;
        this.courses = [];
        this.loadAll();
    }

    loadPage(page) {
        this.page = page;
        this.loadAll();
    }

    clear() {
        this.courses = [];
        this.links = {
            last: 0
        };
        this.page = 0;
        this.predicate = 'id';
        this.reverse = true;
        this.currentSearch = '';
        this.currentSearchTemp = 'Search';
        this.loadAll();
    }

    search(query) {
        if (!query) {
            return this.clear();
        }
        this.query = query;
        this.courses = [];
        this.links = {
            last: 0
        };
        this.page = 0;
        this.predicate = '_score';
        this.reverse = false;
        this.currentSearchTemp = query;
        this.isUsaUrl = false;
        this.isCanadaUrl = false;
        if ((location.hostname + this.router.url).includes('aceaol.com')) {
            this.isUsaUrl = true;
        } else if ((location.hostname + this.router.url).includes('aceaol.ca')) {
            this.isCanadaUrl = true;
        } else {
            this.isCanadaUrl = true;
        }
        if (this.isCanadaUrl) {
            this.currentSearch = this.currentSearchTemp + ' && CANADA';
        } else if (this.isUsaUrl) {
            this.currentSearch = this.currentSearchTemp + ' && USA';
        } else {
            this.currentSearch = this.currentSearchTemp;
        }
        this.loadAll();
    }

    public reloadPage() {
        location.reload();
    }

    ngAfterViewInit(): void {
        this.spinner.show();
        setTimeout(() => {
            /** spinner ends after 5 seconds */
            this.spinner.hide();
        }, 4000);
    }

    /*private isDetected() {
        let detected = false;
        // create the bait
        const bait = document.createElement('div');
        bait.id = 'nWarBteAmkpQ';
        bait.style.display = 'none';
        window.document.body.appendChild(bait);
        if (document.getElementById('nWarBteAmkpQ')) {
            detected = false;
        } else {
            detected = true;
        }
        // destroy the bait
        window.document.body.removeChild(bait);
        console.log('Inside isDetected function: ' + detected);
        return detected;
    }*/

    checkAdBlock() {
        if (this.navbarService.getAdBlocked()) {
            this.errorAdBlock = true;
        } else {
            this.errorAdBlock = false;
        }
    }

    ngOnInit() {
        this.isCanada = this.navbarService.getifCanada();
        this.isUsa = this.navbarService.getifUSA();
        this.checkRegionUrl();
        if (this.router.url.includes('/course', 0)) {
            setInterval(() => {
                this.checkAdBlock();
            }, 5000);
        }
        if (this.courseService.getTopic() !== 'undefined') {
            this.search(this.courseService.getTopic());
            this.courseService.resetTopic();
        } else {
            this.loadAll();
        }
        this.principal.identity().then(account => {
            this.currentAccount = account;
            this.userService.getuserbylogin(account.login).subscribe(users => {
                this.userID = users;
                this.customerService.getuser(this.userID).subscribe(customer => {
                    this.customer = customer;
                    this.cartService.check(this.customer.id).subscribe(carts => {
                        this.cart = carts;
                        this.courseCartService.getcollection(this.cart.id).subscribe(bridges => {
                            this.bridgeCart = bridges;
                        });
                    });
                });
            });
        });
        this.registerChangeInCourses();
    }

    checkRegionUrl() {
        if (this.isCanada && location.hostname.includes('aceaol.com')) {
            this.errorRegionLock = true;
            this.shouldnotbeinUsa = true;
        } else if (this.isUsa && location.hostname.includes('aceaol.ca')) {
            this.errorRegionLock = true;
            this.shouldnotbeinCanada = true;
        } else if (!this.isCanada && !this.isUsa) {
            this.errorRegionLock = true;
            this.regionOutside = true;
        }
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: ICourse) {
        return item.id;
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }
    /** else if (this.courseService.check(course.id, this.customer.id)) {
            this.message = 'You have already purchased this course';
        }*/

    public addCourse(course: ICourse) {
        const resourceCheckUrl = SERVER_API_URL + 'api/check/courses';
        this.flagTemp = this.findinCart(course);
        if (this.flagTemp === 1) {
            console.log('add course - customer is ' + this.customer);
            console.log('course id is ' + course.id);
            console.log('cart id is ' + this.cart.id);
            console.log('customer id is' + this.customer.id);
            this.courseService.check(course.id, this.customer.id, this.cart.id).subscribe(flag => {
                console.log('flag is ' + flag);
                if (flag) {
                    this.newBridgeCart = new CourseCartBridge();
                    this.newBridgeCart.course = course;
                    this.newBridgeCart.cart = this.cart;
                    this.newBridgeCart.timestamp = moment();
                    if (this.bridgeCart == null) {
                        this.subscribeToSaveResponse(this.courseCartService.create(this.newBridgeCart));
                        this.bridgeCart[0] = this.newBridgeCart;
                    } else {
                        this.subscribeToSaveResponse(this.courseCartService.create(this.newBridgeCart));
                        this.bridgeCart[this.bridgeCart.length] = this.newBridgeCart;
                    }
                    this.navbarService.noCheckout();
                    this.navbarService.shouldCheck();
                    this.showMessage('Course Added');
                    this.message = 'Course Added';
                    this.flagTemp = 0;
                    /*this.showMessage('You have already purchased this course');*/
                } else {
                    this.message =
                        'You have already purchased this course or access to it has expired. Please ' +
                        ' contact our support for assistance.';
                }
            });
        }
    }

    showMessage(msg: string) {
        setInterval(() => {
            this.message = msg;
        }, 7000);
        this.message = '';
    }

    /**async save(temp: ICourseCartBridge) {
        this.isSaving = true;
        if (temp.id !== undefined) {
            this.subscribeToSaveResponse(this.courseCartService.update(temp));
        } else {
            this.subscribeToSaveResponse(this.courseCartService.create(temp));
        }
    }*/

    subscribeToSaveResponse(result: Observable<HttpResponse<ICourseCartBridge>>) {
        result.subscribe((res: HttpResponse<ICourseCartBridge>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    onSaveSuccess() {
        this.isSaving = false;
    }

    onSaveError() {
        this.isSaving = false;
    }

    findinCart(course: ICourse) {
        for (let i = 0; i < this.bridgeCart.length; i++) {
            if (this.bridgeCart[i].course.id === course.id) {
                /*this.showMessage('This course is already added to your cart');*/
                this.message = 'This course is already added to your cart';
                return 2;
            }
        }
        return 1;
    }

    registerChangeInCourses() {
        this.eventSubscriber = this.eventManager.subscribe('courseListModification', response => this.reset());
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    protected paginateCourses(data: ICourse[], headers: HttpHeaders) {
        this.links = this.parseLinks.parse(headers.get('link'));
        this.totalItems = parseInt(headers.get('X-Total-Count'), 10);
        for (let i = 0; i < data.length; i++) {
            this.courses.push(data[i]);
        }
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
