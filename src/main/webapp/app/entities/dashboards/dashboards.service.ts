import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IDashboards } from 'app/shared/model/dashboards.model';

type EntityResponseType = HttpResponse<IDashboards>;
type EntityArrayResponseType = HttpResponse<IDashboards[]>;

@Injectable({ providedIn: 'root' })
export class DashboardsService {
    private resourceUrl = SERVER_API_URL + 'api/dashboards';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/dashboards';

    constructor(private http: HttpClient) {}

    create(dashboards: IDashboards): Observable<EntityResponseType> {
        return this.http.post<IDashboards>(this.resourceUrl, dashboards, { observe: 'response' });
    }

    update(dashboards: IDashboards): Observable<EntityResponseType> {
        return this.http.put<IDashboards>(this.resourceUrl, dashboards, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IDashboards>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IDashboards[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IDashboards[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
