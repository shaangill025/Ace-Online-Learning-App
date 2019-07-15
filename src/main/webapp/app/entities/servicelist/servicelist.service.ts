import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IServicelist } from 'app/shared/model/servicelist.model';

type EntityResponseType = HttpResponse<IServicelist>;
type EntityArrayResponseType = HttpResponse<IServicelist[]>;

@Injectable({ providedIn: 'root' })
export class ServicelistService {
    private resourceUrl = SERVER_API_URL + 'api/servicelists';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/servicelists';

    constructor(private http: HttpClient) {}

    create(servicelist: IServicelist): Observable<EntityResponseType> {
        return this.http.post<IServicelist>(this.resourceUrl, servicelist, { observe: 'response' });
    }

    update(servicelist: IServicelist): Observable<EntityResponseType> {
        return this.http.put<IServicelist>(this.resourceUrl, servicelist, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IServicelist>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IServicelist[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IServicelist[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
