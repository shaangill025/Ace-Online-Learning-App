import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IOrders } from 'app/shared/model/orders.model';
import { ICart } from 'app/shared/model/cart.model';

type EntityResponseType = HttpResponse<IOrders>;
type EntityArrayResponseType = HttpResponse<IOrders[]>;

@Injectable({ providedIn: 'root' })
export class OrdersService {
    private resourceUrl = SERVER_API_URL + 'api/orders';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/orders';
    private resourceOrderCartUrl = SERVER_API_URL + 'api/cart/order';

    constructor(private http: HttpClient) {}

    create(orders: IOrders): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(orders);
        return this.http
            .post<IOrders>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(orders: IOrders): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(orders);
        return this.http
            .put<IOrders>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<IOrders>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IOrders[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IOrders[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    getoptionalcart(id: number): Observable<HttpResponse<IOrders>> {
        return this.http.get<IOrders>(`${SERVER_API_URL + 'api/cart/order/optional'}/${id}`, {observe: 'response'});
    }

    getsinglecart(id: number): Observable<IOrders> {
        return this.http.get<IOrders>(`${this.resourceOrderCartUrl}/${id}`);
    }

    private convertDateFromClient(orders: IOrders): IOrders {
        const copy: IOrders = Object.assign({}, orders, {
            createddate: orders.createddate != null && orders.createddate.isValid() ? orders.createddate.toJSON() : null
        });
        return copy;
    }

    private convertDateFromServer(res: EntityResponseType): EntityResponseType {
        res.body.createddate = res.body.createddate != null ? moment(res.body.createddate) : null;
        return res;
    }

    private convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        res.body.forEach((orders: IOrders) => {
            orders.createddate = orders.createddate != null ? moment(orders.createddate) : null;
        });
        return res;
    }
}
