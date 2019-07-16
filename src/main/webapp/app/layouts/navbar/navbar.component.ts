import { AfterViewInit, ChangeDetectorRef, Component, DoCheck, EventEmitter, NgZone, OnChanges, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiLanguageService } from 'ng-jhipster';
import { Location } from '@angular/common';
import { SERVER_API_URL, VERSION } from 'app/app.constants';
import { JhiLanguageHelper, Principal, LoginModalService, LoginService, Account, IUser, UserService } from 'app/core';
import { ProfileService } from '../profiles/profile.service';
import { ICustomer } from 'app/shared/model/customer.model';
import { ICart } from 'app/shared/model/cart.model';
import { ICourseCartBridge } from 'app/shared/model/course-cart-bridge.model';
import { CourseService } from 'app/entities/course';
import { CustomerService } from 'app/entities/customer';
import { CartService } from 'app/entities/cart';
import { CourseCartBridgeService } from 'app/entities/course-cart-bridge';
import { NavbarService } from 'app/layouts/navbar/navbar.service';
import { HttpClient } from '@angular/common/http';
import * as setInterval from 'core-js/library/fn/set-interval';

@Component({
    selector: 'jhi-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['navbar.scss']
})
export class NavbarComponent implements OnInit, AfterViewInit {
    inProduction: boolean;
    isNavbarCollapsed: boolean;
    languages: any[];
    currentAccount: Account;
    courseNumber: number;
    user: IUser;
    userID = 0;
    customer: ICustomer;
    cart: ICart;
    bridgeCart: ICourseCartBridge[];
    tempCart: ICart;
    swaggerEnabled: boolean;
    modalRef: NgbModalRef;
    version: string;
    flag = false;
    isCourse = false;
    isCheckout = false;
    moveToOtherSite = false;
    adminRole = false;
    userRole = false;
    numberCnt = 0;
    cartNavbar: ICart;
    isCanada = false;
    isUSA = false;
    countDown = 0;
    passwordReset = false;
    cartAnimation = false;
    constructor(
        private loginService: LoginService,
        private languageService: JhiLanguageService,
        private languageHelper: JhiLanguageHelper,
        private loginModalService: LoginModalService,
        private profileService: ProfileService,
        private router: Router,
        private principal: Principal,
        private courseService: CourseService,
        private customerService: CustomerService,
        private cartService: CartService,
        private courseCartService: CourseCartBridgeService,
        private userService: UserService,
        public navbarService: NavbarService,
        private ref: ChangeDetectorRef,
        private location: Location,
        private http: HttpClient
    ) {
        this.version = VERSION ? 'v' + VERSION : '';
        this.isNavbarCollapsed = true;
        this.bridgeCart = [];
    }

    /**ngDoCheck() {
        this.courseCartService.getcollection(this.cart.id).subscribe(bridges => {
            this.bridgeCart = bridges;
            if (this.bridgeCart.length !== this.courseNumber) {
                this.ngOnInit();
            }
        });
    }*/

    /**ngDoCheck() {
        this.courseCartService.getcollection(this.cart.id).subscribe(bridges => {
            this.bridgeCart = bridges;
            if (this.bridgeCart.length !== this.courseNumber) {
                this.ngOnInit();
            }
        });
    }*/

    ngAfterViewInit(): void {
        this.navbarService.getcountryfromip();
        if (document.getElementById('YpceOtLqEXgu')) {
            this.navbarService.isAdIsNotBlocked();
        } else {
            this.navbarService.isAdIsBlocked();
        }
        setInterval(() => {
            if (this.principal.hasAnyAuthorityDirect(['ROLE_ADMIN'])) {
                this.adminRole = true;
            }
            if (this.principal.hasAnyAuthorityDirect(['ROLE_USER'])) {
                this.userRole = true;
            }
            this.isCourse = this.router.url.includes('/course', 0);
            this.isCheckout = this.router.url.includes('/checkout', 0);
            if (this.isCourse || this.isCheckout) {
                if (this.navbarService.getCheck()) {
                    this.cartAnimation = true;
                }
                if (!this.navbarService.getCheck()) {
                    this.cartAnimation = false;
                }
                if (this.navbarService.getCheckout()) {
                    this.cartAnimation = false;
                }
            }
        }, 1000);
    }

    ngOnInit() {
        // this.courseNumber = this.update();
        this.navbarService.getcountryfromip();
        if (!this.router.url.includes('reset/finish?key')) {
            this.countDown = 10;
            this.passwordReset = false;
            setInterval(() => {
                if (this.countDown >= 1) {
                    this.countDown--;
                }
            }, 1000);
            if ((location.hostname + this.router.url).includes('ace-online.herokuapp.com')) {
                this.moveToOtherSite = true;
                this.navbarService.setMovetoOtherSite(true);
                setTimeout(() => {
                    console.log('is Canada before redirecting' + this.navbarService.getifCanada());
                    console.log('is USA before redirecting' + this.navbarService.getifCanada());
                    if (this.navbarService.getifCanada()) {
                        location.href = 'https://www.aceaol.ca/#/';
                    } else if (this.navbarService.getifUSA()) {
                        location.href = 'http://www.aceaol.com/#/';
                    } else {
                        location.href = 'https://www.aceaol.ca/#/';
                    }
                }, 10000);
            }
        } else {
            this.countDown = 60;
            this.passwordReset = true;
            setInterval(() => {
                if (this.countDown >= 1) {
                    this.countDown--;
                }
            }, 1000);
            if ((location.hostname + this.router.url).includes('ace-online.herokuapp.com')) {
                this.moveToOtherSite = true;
                this.navbarService.setMovetoOtherSite(true);
                setTimeout(() => {
                    console.log('is Canada before redirecting' + this.navbarService.getifCanada());
                    console.log('is USA before redirecting' + this.navbarService.getifCanada());
                    if (this.countDown === 0) {
                        if (this.navbarService.getifCanada()) {
                            location.href = 'https://www.aceaol.ca/#/';
                        } else if (this.navbarService.getifUSA()) {
                            location.href = 'http://www.aceaol.com/#/';
                        } else {
                            location.href = 'https://www.aceaol.ca/#/';
                        }
                    }
                }, 60500);
            }
        }
        this.principal.identity().then(account => {
            this.currentAccount = account;
            console.log('User Login' + this.currentAccount.login);
            this.userService.getuserbylogin(this.currentAccount.login).subscribe(users => {
                this.userID = users;
                this.customerService.getuser(this.currentAccount.login).subscribe(customer => {
                    this.customer = customer;
                    this.cartService.check(this.customer.id).subscribe(carts => {
                        this.cart = carts;
                        this.courseCartService.getcount(this.cart.id).subscribe(courseNum => {
                            if (courseNum >= 1) {
                                this.cartAnimation = true;
                                this.navbarService.shouldCheck();
                            } else {
                                this.cartAnimation = false;
                            }
                        });
                    });
                });
            });
        });
        this.languageHelper.getAll().then(languages => {
            this.languages = languages;
        });
        this.profileService.getProfileInfo().then(profileInfo => {
            this.inProduction = profileInfo.inProduction;
            this.swaggerEnabled = profileInfo.swaggerEnabled;
        });
        // this.courseNumber = this.navbarService.courses;
    }

    changeLanguage(languageKey: string) {
        this.languageService.changeLanguage(languageKey);
    }

    collapseNavbar() {
        this.isNavbarCollapsed = true;
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
        // this.checkRole = this.principal.hasAnyAuthorityDirect(['ROLE_USER', 'ROLE_ADMIN']);
        if (this.principal.hasAnyAuthorityDirect(['ROLE_ADMIN'])) {
            this.adminRole = true;
        }
        if (this.principal.hasAnyAuthorityDirect(['ROLE_USER'])) {
            this.userRole = true;
        }
    }

    logout() {
        this.collapseNavbar();
        this.loginService.logout();
        this.router.navigate(['']);
        this.adminRole = false;
        this.userRole = false;
    }

    anonymousLogin() {
        this.loginService.logout();
        this.modalRef = this.loginModalService.open();
        // this.checkRole = this.principal.hasAnyAuthorityDirect(['ROLE_USER', 'ROLE_ADMIN']);
        if (this.principal.hasAnyAuthorityDirect(['ROLE_ADMIN'])) {
            this.adminRole = true;
        }
        if (this.principal.hasAnyAuthorityDirect(['ROLE_USER'])) {
            this.userRole = true;
        }
    }

    toggleNavbar() {
        this.isNavbarCollapsed = !this.isNavbarCollapsed;
    }

    getImageUrl() {
        return this.isAuthenticated() ? this.principal.getImageUrl() : null;
    }
}
