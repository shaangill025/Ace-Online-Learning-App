import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ICompanyRequest } from 'app/shared/model/company-request.model';

type EntityResponseType = HttpResponse<ICompanyRequest>;
type EntityArrayResponseType = HttpResponse<ICompanyRequest[]>;

@Injectable({ providedIn: 'root' })
export class CompanyRequestService {
    private resourceUrl = SERVER_API_URL + 'api/company-requests';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/company-requests';

    constructor(private http: HttpClient) {}

    create(companyRequest: ICompanyRequest): Observable<EntityResponseType> {
        return this.http.post<ICompanyRequest>(this.resourceUrl, companyRequest, { observe: 'response' });
    }

    update(companyRequest: ICompanyRequest): Observable<EntityResponseType> {
        return this.http.put<ICompanyRequest>(this.resourceUrl, companyRequest, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ICompanyRequest>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ICompanyRequest[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ICompanyRequest[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
