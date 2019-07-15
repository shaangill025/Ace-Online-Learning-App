import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ITags } from 'app/shared/model/tags.model';

type EntityResponseType = HttpResponse<ITags>;
type EntityArrayResponseType = HttpResponse<ITags[]>;

@Injectable({ providedIn: 'root' })
export class TagsService {
    private resourceUrl = SERVER_API_URL + 'api/tags';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/tags';

    constructor(private http: HttpClient) {}

    create(tags: ITags): Observable<EntityResponseType> {
        return this.http.post<ITags>(this.resourceUrl, tags, { observe: 'response' });
    }

    update(tags: ITags): Observable<EntityResponseType> {
        return this.http.put<ITags>(this.resourceUrl, tags, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ITags>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ITags[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ITags[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
