import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { JhiAlertService, JhiEventManager } from 'ng-jhipster';
import { ICheckout } from 'app/shared/model/checkout.model';
import { IUser, Principal, UserService } from 'app/core';
import { CourseService } from 'app/entities/course';
import { CustomerService } from 'app/entities/customer';
import { CartService } from 'app/entities/cart';
import { CourseCartBridgeService } from 'app/entities/course-cart-bridge';
import { ICustomer } from 'app/shared/model/customer.model';
import { ICart } from 'app/shared/model/cart.model';
import { ICourseCartBridge } from 'app/shared/model/course-cart-bridge.model';
import { NavbarService } from 'app/layouts/navbar/navbar.service';
import { IOrders, NOTIFICATIONS, PAYMENT } from 'app/shared/model/orders.model';
import { OrdersService } from 'app/entities/orders';
import * as moment from 'moment';
import { now } from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared';
import { SERVER_API_URL } from 'app/app.constants';
// import { StripeService, StripeCardComponent, ElementOptions, ElementsOptions } from 'ngx-stripe';
import { StripeService, Elements, Element as StripeElement, ElementsOptions } from 'ngx-stripe';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
    selector: 'jhi-checkout',
    templateUrl: './checkout.component.html',
    styleUrls: ['checkout.css']
})
export class CheckoutComponent implements OnInit, OnDestroy {
    currentAccount: any;
    eventSubscriber: Subscription;
    currentSearch: string;
    courseNumber = 0;
    user: IUser;
    userID = 0;
    customer: ICustomer;
    cart: ICart;
    amount = 0;
    points = 0;
    tax = 0;
    gross = 0;
    bridgeCart: ICourseCartBridge[];
    tempCustPoints = 0;
    tempCart: ICart;
    flag = false;
    isSaving: boolean;
    transactionError = '';
    redeemPointsText = 'Redeem Points';
    redeempoints: number;
    redeemApprPts = 0;
    isUsaUrl = false;
    countryVar = 'us';
    currency = 'USD';
    isCanadaUrl = false;
    // previousCourseId = [];
    previousCourseId = 0;
    constructor(
        private jhiAlertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private activatedRoute: ActivatedRoute,
        private principal: Principal,
        private courseService: CourseService,
        private customerService: CustomerService,
        private cartService: CartService,
        private courseCartService: CourseCartBridgeService,
        private userService: UserService,
        private navbarService: NavbarService,
        private http: HttpClient,
        private orderService: OrdersService,
        private router: Router
        //private spinner: NgxSpinnerService
    ) {}

    chargeCreditCard() {
        this.transactionError = '';
        const form = document.getElementsByTagName('form')[0];
        (<any>window).Stripe.card.createToken(
            {
                number: form.cardNumber.value,
                exp_month: form.expMonth.value,
                exp_year: form.expYear.value,
                cvc: form.cvc.value
            },
            (status: number, response: any) => {
                if (status === 200) {
                    const token = response.id;
                    console.log('tokenInit:' + token);
                    this.chargeCard(token);
                } else {
                    this.transactionError = response.error.message;
                    console.log(response.error.message);
                }
            }
        );
    }

    public redeemPts() {
        if (this.customer.points <= this.redeempoints) {
            this.redeemApprPts = this.redeempoints;
            this.redeempoints = 0;
            this.redeemPointsText = 'Reedeemed! Points Adjusted';
        } else {
            this.redeempoints = 0;
            this.redeemPointsText = 'Not Enough Points';
        }
    }

    chargeCard(token: string) {
        const tokenStr: string = token;
        if ((location.hostname + this.router.url).includes('aceaol.com')) {
            this.isUsaUrl = true;
            this.countryVar = 'us';
        } else if ((location.hostname + this.router.url).includes('aceaol.ca')) {
            this.isCanadaUrl = true;
            this.countryVar = 'canada';
        } else {
            this.countryVar = 'us';
        }
        const headers = new HttpHeaders({
            token: tokenStr,
            amount: String(this.gross),
            cartId: String(this.cart.id),
            redeem: String(this.redeemApprPts),
            buypoints: String(this.points),
            currency: String(this.countryVar)
        });
        console.log('token:' + token);
        console.log('amount' + String(this.gross));
        this.http.post(SERVER_API_URL + 'api/carts/charge', {}, { headers }).subscribe((resp: IOrders) => {
            if (resp.seller_status === 'succeeded') {
                this.transactionError = 'Order has been completed, payment [' + resp.gateway_id + '] has been accepted.';
                const resourceCheckoutUrl = SERVER_API_URL + 'api/checkout/carts';
                this.navbarService.shouldCheck();
                this.navbarService.isCheckout();
                /*this.orderService.getsinglecart(this.cart.id).subscribe(order => {
                    const newOrder: IOrders = order;
                    newOrder.card_type = resp['source.brand'];
                    newOrder.last4 = resp['source.last4'];
                    newOrder.network_status = resp['outcome.network_status'];
                    newOrder.seller_message = resp['outcome.seller_message'];
                    newOrder.seller_type = resp['destination'];
                    this.orderService.update(newOrder);
                });*/
                setTimeout(() => {
                    this.initialize();
                    this.router.navigateByUrl(SERVER_API_URL + 'dashboards');
                }, 5000);
            } else if (resp.seller_status === 'pending') {
                this.transactionError = 'Order is under process, payment [' + resp.gateway_id + '] is pending.';
                const resourceCheckoutUrl = SERVER_API_URL + 'api/checkout/carts';
                this.navbarService.isCheckout();
                /*this.orderService.getsinglecart(this.cart.id).subscribe(order => {
                    const newOrder: IOrders = order;
                    newOrder.card_type = resp['source.brand'];
                    newOrder.last4 = resp['source.last4'];
                    newOrder.network_status = resp['outcome.network_status'];
                    newOrder.seller_message = resp['outcome.seller_message'];
                    newOrder.seller_type = resp['destination'];
                    this.orderService.update(newOrder);
                });*/
                setTimeout(() => {
                    this.initializeAfterSale();
                    this.router.navigateByUrl(SERVER_API_URL + 'dashboards');
                }, 5000);
            }
        });
    }

    public disableBtn(instance: ICourseCartBridge) {
        // if (this.previousCourseId.includes(instance.id)) {
        if (this.previousCourseId === instance.id) {
            return true;
        }
        return false;
    }

    public removeCourse(instance: ICourseCartBridge) {
        const flagTemp = this.disableBtn(instance);
        if (!flagTemp) {
            // this.previousCourseId.push(instance.id);
            this.previousCourseId = instance.id;
            const resourceChangeUrl = SERVER_API_URL + 'api/change/carts';
            this.http.put(`${resourceChangeUrl}/${this.cart.id}?identifier=` + instance.course.id, {}).subscribe(
                data => {
                    console.log('PUT Request is successful ', data);
                },
                error => {
                    console.log('Error', error);
                }
            );
            this.courseCartService.delete(instance.id).subscribe(() => {
                this.courseCartService.getcollection(this.cart.id).subscribe(bridges => {
                    if (this.amount - instance.course.amount >= 0) {
                        this.amount -= instance.course.amount;
                    }
                    if (this.points - instance.course.point >= 0) {
                        this.points -= instance.course.point;
                    }
                    this.tax = this.amount * 0.05;
                    this.gross = this.amount + this.tax;
                    this.displayAchievementPoint();
                    this.bridgeCart = bridges;
                    if (this.cart.checkout) {
                        this.courseNumber = 0;
                    } else {
                        this.courseNumber = this.bridgeCart.length;
                    }
                    this.navbarService.donotCheck();
                });
            });
        }
    }

    initialize() {
        this.amount = 0;
        this.points = 0;
        this.tax = this.amount * 0.05;
        this.gross = this.amount + this.tax;
        this.displayAchievementPoint();
        this.bridgeCart = null;
        this.transactionError = '';
    }

    initializeAfterSale() {
        this.amount = 0;
        this.points = 0;
        this.tax = this.amount * 0.05;
        this.gross = this.amount + this.tax;
        this.displayAchievementPoint();
        this.bridgeCart = null;
        this.transactionError = 'Transaction Successful';
        this.courseNumber = 0;
        this.bridgeCart = [];
    }

    subscribeToSaveResponseOrder(result: Observable<HttpResponse<IOrders>>) {
        result.subscribe((res: HttpResponse<IOrders>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    subscribeToSaveResponseCart(result: Observable<HttpResponse<ICart>>) {
        result.subscribe((res: HttpResponse<ICart>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    subscribeToSaveResponse(result: Observable<HttpResponse<ICart>>) {
        result.subscribe((res: HttpResponse<ICart>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    onSaveSuccess() {
        this.isSaving = false;
    }

    onSaveError() {
        this.isSaving = false;
    }

    displayAchievementPoint() {
        return this.customer.points;
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

    ngOnInit() {
        if ((location.hostname + this.router.url).includes('aceaol.com')) {
            this.currency = 'USD';
        } else if ((location.hostname + this.router.url).includes('aceaol.ca')) {
            this.currency = 'CAD';
        } else {
            this.currency = 'USD';
        }
        this.principal.identity().then(account => {
            this.currentAccount = account;
            this.userService.getuserbylogin(account.login).subscribe(users => {
                this.userID = users;
                this.customerService.getuser(this.currentAccount.login).subscribe(customer => {
                    this.customer = customer;
                    this.tempCustPoints = this.customer.points;
                    this.cartService.check(this.customer.id).subscribe(carts => {
                        this.cart = carts;
                        this.courseCartService.getcollection(this.cart.id).subscribe(bridges => {
                            this.bridgeCart = bridges;
                            if (this.cart.checkout) {
                                this.courseNumber = 0;
                            } else {
                                this.courseNumber = this.bridgeCart.length;
                            }
                            for (let i = 0; i < this.bridgeCart.length; i++) {
                                this.amount += this.bridgeCart[i].course.amount;
                                this.points += this.bridgeCart[i].course.point;
                            }
                            this.tax = this.amount * 0.05;
                            this.gross = this.amount + this.tax - this.redeemApprPts;
                            this.displayAchievementPoint();
                        });
                    });
                });
            });
        });
    }
    ngOnDestroy() {}

    trackId(index: number, item: ICheckout) {
        return item.id;
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
