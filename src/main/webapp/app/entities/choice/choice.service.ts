import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IChoice } from 'app/shared/model/choice.model';

type EntityResponseType = HttpResponse<IChoice>;
type EntityArrayResponseType = HttpResponse<IChoice[]>;

@Injectable({ providedIn: 'root' })
export class ChoiceService {
    private resourceUrl = SERVER_API_URL + 'api/choices';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/choices';

    constructor(private http: HttpClient) {}

    create(choice: IChoice): Observable<EntityResponseType> {
        return this.http.post<IChoice>(this.resourceUrl, choice, { observe: 'response' });
    }

    update(choice: IChoice): Observable<EntityResponseType> {
        return this.http.put<IChoice>(this.resourceUrl, choice, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IChoice>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IChoice[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IChoice[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
