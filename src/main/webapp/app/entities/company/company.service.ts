import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ICompany } from 'app/shared/model/company.model';

type EntityResponseType = HttpResponse<ICompany>;
type EntityArrayResponseType = HttpResponse<ICompany[]>;

@Injectable({ providedIn: 'root' })
export class CompanyService {
    private resourceUrl = SERVER_API_URL + 'api/companies';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/companies';
    private resourceCompanyUrl = SERVER_API_URL + 'api/company';

    constructor(private http: HttpClient) {}

    create(company: ICompany): Observable<EntityResponseType> {
        return this.http.post<ICompany>(this.resourceUrl, company, { observe: 'response' });
    }

    update(company: ICompany): Observable<EntityResponseType> {
        return this.http.put<ICompany>(this.resourceUrl, company, { observe: 'response' });
    }

    createfromrequest(id: number): Observable<ICompany> {
        return this.http.post<ICompany>(`${this.resourceCompanyUrl}/${id}`, {});
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ICompany>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ICompany[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ICompany[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
