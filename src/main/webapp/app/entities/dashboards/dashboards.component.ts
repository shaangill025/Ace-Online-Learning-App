import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiParseLinks, JhiAlertService } from 'ng-jhipster';
import { IDashboards } from 'app/shared/model/dashboards.model';
import { Account, Principal, UserService } from 'app/core';
import { ITEMS_PER_PAGE } from 'app/shared';
import { DashboardsService } from './dashboards.service';
import { ICourseHistory } from 'app/shared/model/course-history.model';
import { ISectionHistory } from 'app/shared/model/section-history.model';
import { ICertificate } from 'app/shared/model/certificate.model';
import { ICourse } from 'app/shared/model/course.model';
import { ISection } from 'app/shared/model/section.model';
import { ICart } from 'app/shared/model/cart.model';
import { CustomerService } from 'app/entities/customer';
import { CartService } from 'app/entities/cart';
import { SectionHistoryService } from 'app/entities/section-history';
import { CertificateService } from 'app/entities/certificate';
import { CourseHistoryService } from 'app/entities/course-history';
import { SectionService } from 'app/entities/section';
import { IOrders } from 'app/shared/model/orders.model';
import { OrdersService } from 'app/entities/orders';
import { ICustomer } from 'app/shared/model/customer.model';
// import moment = require("moment");

@Component({
    selector: 'jhi-dashboards',
    templateUrl: './dashboards.component.html'
})
export class DashboardsComponent implements OnInit {
    dashboards: IDashboards[];
    currentAccount: any;
    eventSubscriber: Subscription;
    itemsPerPage: number;
    links: any;
    pageCert: any;
    pageOrder: any;
    pageCourse: any;
    pageCart: any;
    predicate: any;
    queryCount: any;
    reverse: any;
    totalItems: number;
    currentSearch: string;
    account: Account;
    custEmail: string;
    courses: ICourseHistory[];
    sections: ISectionHistory[];
    certificates: ICertificate[];
    recentCourse: ICourse;
    recentSection: ISection;
    carts: ICart[];
    orders: IOrders[];
    courseHistory: ICourseHistory[];
    tempCourseHistory: ICourseHistory[];
    sectionHistory: ISectionHistory[];
    customer: ICustomer;

    constructor(
        private dashboardsService: DashboardsService,
        private jhiAlertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private parseLinks: JhiParseLinks,
        private activatedRoute: ActivatedRoute,
        private principal: Principal,
        private customerService: CustomerService,
        private userService: UserService,
        private cartService: CartService,
        private sectionHistoryService: SectionHistoryService,
        private certificateService: CertificateService,
        private courseHistoryService: CourseHistoryService,
        private sectionService: SectionService,
        private orderService: OrdersService,
        private router: Router
        //private spinner: NgxSpinnerService
    ) {
        this.dashboards = [];
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.pageCert = 0;
        this.pageOrder = 0;
        this.pageCourse = 0;
        this.pageCart = 0;
        this.links = {
            last: 0
        };
        this.predicate = 'id';
        this.reverse = true;
        this.currentSearch =
            this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search']
                ? this.activatedRoute.snapshot.params['search']
                : '';
        this.custEmail = '';
        this.courses = [];
        this.sections = [];
        this.certificates = [];
        this.recentCourse = {};
        this.recentSection = {};
        this.carts = [];
        this.orders = [];
        this.courseHistory = [];
        this.tempCourseHistory = [];
        this.sectionHistory = [];
        this.customer = {};
    }

    ngOnInit() {
        this.principal.identity().then(account => {
            /*this.spinner.show();
            setTimeout(() => {
                // spinner ends after 5 seconds
                this.spinner.hide();
            }, 5000);*/
            this.currentAccount = account;
            this.custEmail = this.currentAccount.email;
            this.userService.getuserbylogin(this.currentAccount.login).subscribe(userId => {
                this.customerService.getuser(this.currentAccount.login).subscribe(customer => {
                    this.customer = customer;
                    this.cartService.getcustomer(customer.id).subscribe(cart => {
                        this.carts = cart.body;
                        this.carts.forEach(tempCart => {
                            this.orderService.getoptionalcart(tempCart.id).subscribe(order => {
                                this.orders.push(order.body);
                            });
                        });
                    });
                    this.sectionHistoryService.getcustomer(customer.id).subscribe(sechistData => {
                        if (sechistData) {
                            this.sectionHistory = sechistData;
                            console.log(this.sectionHistory);
                        }
                    });
                    this.courseHistoryService.getcustomer(customer.id).subscribe(coursehistData => {
                        this.tempCourseHistory = coursehistData.body;
                        for (const temp of this.tempCourseHistory) {
                            if (temp.isactive) {
                                this.courseHistory.push(temp);
                            }
                        }
                        console.log(this.courseHistory);
                    });
                    this.certificateService.getCustomerCount(customer.id).subscribe(numCert => {
                        if (numCert > 0) {
                            this.certificateService.getcustomer(customer.id).subscribe(certData => {
                                this.certificates = certData.body;
                            });
                        }
                    });

                    this.sectionHistoryService.getrecent(customer.id).subscribe(recentSecId => {
                        if (recentSecId.id !== -1) {
                            this.recentSection = recentSecId;
                            // this.recentSection = recentSec;
                            console.log(this.recentSection);
                            console.log(this.recentSection.normSection);
                        }
                    });
                    this.courseHistoryService.getrecent(customer.id).subscribe(lastCourse => {
                        this.recentCourse = lastCourse.body;
                        console.log(this.recentCourse);
                        console.log(this.recentCourse.normCourses);
                    });
                });
            });
        });
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    recCourse2(course: ICourse) {
        this.sectionHistoryService.getpersistance(this.customer.id, course.id).subscribe(recentSec => {
            this.router.navigateByUrl('section/' + recentSec.section.id.toString() + '/view');
        });
    }

    recCart(cart: ICart) {
        this.cartService.find(cart.id).subscribe(recentCart => {
            this.router.navigateByUrl('cart/' + cart.id.toString() + '/view');
        });
    }

    recCourse() {
        this.sectionHistoryService.getpersistance(this.customer.id, this.recentCourse.id).subscribe(recentSec => {
            this.router.navigateByUrl('section/' + recentSec.section.id.toString() + '/view');
        });
    }

    recSection() {
        this.router.navigateByUrl('section/' + this.recentSection.id.toString() + '/view');
    }

    recCertificate(cert: ICertificate) {
        this.router.navigateByUrl('certificate/' + cert.id.toString() + '/view');
    }

    resetCartPage() {
        this.pageCart = 0;
    }

    loadCartPage(page) {
        this.pageCart = page;
    }

    resetCertPage() {
        this.pageCert = 0;
    }

    loadCertPage(page) {
        this.pageCert = page;
    }

    resetOrderPage() {
        this.pageOrder = 0;
    }

    loadOrderPage(page) {
        this.pageOrder = page;
    }

    resetCoursePage() {
        this.pageCourse = 0;
    }

    loadCoursePage(page) {
        this.pageCourse = page;
    }
}
