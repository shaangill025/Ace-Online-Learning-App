import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ISection } from 'app/shared/model/section.model';

type EntityResponseType = HttpResponse<ISection>;
type EntityArrayResponseType = HttpResponse<ISection[]>;

@Injectable({ providedIn: 'root' })
export class SectionService {
    private resourceUrl = SERVER_API_URL + 'api/sections';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/sections';
    private resourceCourseUrl =  SERVER_API_URL + 'api/bycourse/sections';
    private resourceCourseUrl2 =  SERVER_API_URL + 'api/count/bycourse/sections';

    constructor(private http: HttpClient) {}

    create(section: ISection): Observable<EntityResponseType> {
        return this.http.post<ISection>(this.resourceUrl, section, { observe: 'response' });
    }

    update(section: ISection): Observable<EntityResponseType> {
        return this.http.put<ISection>(this.resourceUrl, section, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ISection>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ISection[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ISection[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }

    getbycourse(id: number): Observable<HttpResponse<ISection>> {
        return this.http.get<ISection>(`${this.resourceCourseUrl}/${id}`, { observe: 'response' });
    }

    getcountbycourse(id: number): Observable<HttpResponse<ISection[]>> {
        return this.http.get<ISection[]>(`${this.resourceCourseUrl2}/${id}`, { observe: 'response' });
    }
}
