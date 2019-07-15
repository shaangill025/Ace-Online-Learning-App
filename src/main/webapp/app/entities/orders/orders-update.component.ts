import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';

import { IOrders } from 'app/shared/model/orders.model';
import { OrdersService } from './orders.service';
import { ICart } from 'app/shared/model/cart.model';
import { CartService } from 'app/entities/cart';

@Component({
    selector: 'jhi-orders-update',
    templateUrl: './orders-update.component.html'
})
export class OrdersUpdateComponent implements OnInit {
    private _orders: IOrders;
    isSaving: boolean;

    carts: ICart[];
    createddate: string;

    constructor(
        private jhiAlertService: JhiAlertService,
        private ordersService: OrdersService,
        private cartService: CartService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ orders }) => {
            this.orders = orders;
        });
        this.cartService.query({ filter: 'orders-is-null' }).subscribe(
            (res: HttpResponse<ICart[]>) => {
                if (!this.orders.cart || !this.orders.cart.id) {
                    this.carts = res.body;
                } else {
                    this.cartService.find(this.orders.cart.id).subscribe(
                        (subRes: HttpResponse<ICart>) => {
                            this.carts = [subRes.body].concat(res.body);
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
        this.orders.createddate = moment(this.createddate, DATE_TIME_FORMAT);
        if (this.orders.id !== undefined) {
            this.subscribeToSaveResponse(this.ordersService.update(this.orders));
        } else {
            this.subscribeToSaveResponse(this.ordersService.create(this.orders));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IOrders>>) {
        result.subscribe((res: HttpResponse<IOrders>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

    trackCartById(index: number, item: ICart) {
        return item.id;
    }
    get orders() {
        return this._orders;
    }

    set orders(orders: IOrders) {
        this._orders = orders;
        this.createddate = moment(orders.createddate).format(DATE_TIME_FORMAT);
    }
}
