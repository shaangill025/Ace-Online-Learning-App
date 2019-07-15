import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ILegacyCourses } from 'app/shared/model/legacy-courses.model';

type EntityResponseType = HttpResponse<ILegacyCourses>;
type EntityArrayResponseType = HttpResponse<ILegacyCourses[]>;

@Injectable({ providedIn: 'root' })
export class LegacyCoursesService {
    public resourceUrl = SERVER_API_URL + 'api/legacy-courses';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/legacy-courses';

    constructor(protected http: HttpClient) {}

    create(legacyCourses: ILegacyCourses): Observable<EntityResponseType> {
        return this.http.post<ILegacyCourses>(this.resourceUrl, legacyCourses, { observe: 'response' });
    }

    update(legacyCourses: ILegacyCourses): Observable<EntityResponseType> {
        return this.http.put<ILegacyCourses>(this.resourceUrl, legacyCourses, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ILegacyCourses>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ILegacyCourses[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ILegacyCourses[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
