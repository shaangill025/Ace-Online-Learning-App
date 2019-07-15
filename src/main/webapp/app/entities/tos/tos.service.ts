import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ITos } from 'app/shared/model/tos.model';

type EntityResponseType = HttpResponse<ITos>;
type EntityArrayResponseType = HttpResponse<ITos[]>;

@Injectable({ providedIn: 'root' })
export class TosService {
    public resourceUrl = SERVER_API_URL + 'api/tos';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/tos';

    constructor(protected http: HttpClient) {}

    create(tos: ITos): Observable<EntityResponseType> {
        return this.http.post<ITos>(this.resourceUrl, tos, { observe: 'response' });
    }

    update(tos: ITos): Observable<EntityResponseType> {
        return this.http.put<ITos>(this.resourceUrl, tos, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ITos>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ITos[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ITos[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
