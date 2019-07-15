import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ICustomer } from 'app/shared/model/customer.model';

type EntityResponseType = HttpResponse<ICustomer>;
type EntityArrayResponseType = HttpResponse<ICustomer[]>;

@Injectable({ providedIn: 'root' })
export class CustomerService {
    private resourceUrl = SERVER_API_URL + 'api/customers';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/customers';
    private resourceUserUrl = SERVER_API_URL + 'api/user/customers';
    private resourceHiddenUrl = SERVER_API_URL + 'api/check/account/customers';

    constructor(private http: HttpClient) {}

    create(customer: ICustomer): Observable<EntityResponseType> {
        return this.http.post<ICustomer>(this.resourceUrl, customer, { observe: 'response' });
    }

    update(customer: ICustomer): Observable<EntityResponseType> {
        return this.http.put<ICustomer>(this.resourceUrl, customer, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ICustomer>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    merge(oldid: number, newid: number): Observable<string> {
        return this.http.put<string>(`${SERVER_API_URL + 'api/merge/customers/old'}/${oldid}/${'new'}/${newid}`, {});
    }
    getuser(id: number): Observable<ICustomer> {
        return this.http.get<ICustomer>(`${this.resourceUserUrl}/${id}`);
    }

    constraint(hidden: string): Observable<number> {
        return this.http.get<number>(SERVER_API_URL + 'api/check/account/customer', {
            params: new HttpParams().set('hidden', hidden)
        });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ICustomer[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ICustomer[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
