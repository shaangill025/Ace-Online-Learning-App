import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ICourse } from 'app/shared/model/course.model';
import { ITopic } from 'app/shared/model/topic.model';

type EntityResponseType = HttpResponse<ICourse>;
type EntityArrayResponseType = HttpResponse<ICourse[]>;

@Injectable({ providedIn: 'root' })
export class CourseService {
    public resourceUrl = SERVER_API_URL + 'api/courses';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/courses';
    public resourceCountryUrl = SERVER_API_URL + 'api/courses/country';
    public flag = false;
    public topic = 'undefined';
    constructor(protected http: HttpClient, private route: Router) {}

    create(course: ICourse): Observable<EntityResponseType> {
        return this.http.post<ICourse>(this.resourceUrl, course, { observe: 'response' });
    }

    update(course: ICourse): Observable<EntityResponseType> {
        return this.http.put<ICourse>(this.resourceUrl, course, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ICourse>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    check(courseid: number, customerid: number, cartid: number): Observable<boolean> {
        return this.http.get<boolean>(
            `${SERVER_API_URL + '/api/check'}/${customerid}/${'customer'}/${cartid}/${'cart/courses'}/${courseid}`
        );
    }

    bycountry(country: string): Observable<ICourse[]> {
        return this.http.get<ICourse[]>(`${this.resourceCountryUrl}/${country}`);
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ICourse[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ICourse[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }

    fromTopic(topic: string) {
        this.topic = topic;
    }

    getTopic() {
        return this.topic;
    }

    resetTopic() {
        this.topic = 'undefined';
    }
}
