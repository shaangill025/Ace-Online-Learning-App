import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';
import { HttpParams } from '@angular/common/http';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ICourseHistory } from 'app/shared/model/course-history.model';
import { ISection } from 'app/shared/model/section.model';
import { ICourse } from 'app/shared/model/course.model';
type EntityResponseType = HttpResponse<ICourseHistory>;
type EntityArrayResponseType = HttpResponse<ICourseHistory[]>;
@Injectable({ providedIn: 'root' })
export class CourseHistoryService {
    private resourceUrl = SERVER_API_URL + 'api/course-histories';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/course-histories';
    private resourceCourseUrl = SERVER_API_URL + 'api/recent/course-history';

    constructor(private http: HttpClient) {}

    create(courseHistory: ICourseHistory): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(courseHistory);
        return this.http
            .post<ICourseHistory>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(courseHistory: ICourseHistory): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(courseHistory);
        return this.http
            .put<ICourseHistory>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<ICourseHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<ICourseHistory[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    getrecent(id: number): Observable<HttpResponse<ICourse>> {
        return this.http.get<ICourse>(`${this.resourceCourseUrl}/${id}`, { observe: 'response' });
    }

    getcustomer(id: number): Observable<HttpResponse<ICourseHistory[]>> {
        return this.http.get<ICourseHistory[]>(`${SERVER_API_URL + 'api/customer/course-histories'}/${id}`, { observe: 'response' });
    }

    getbycustomerandcourse(courseid: number, customerid: number): Observable<ICourseHistory> {
        return this.http.get<ICourseHistory>(`${SERVER_API_URL + 'api/customer'}/${customerid}/${'course/course-histories'}/${courseid}`);
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<ICourseHistory[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    private convertDateFromClient(courseHistory: ICourseHistory): ICourseHistory {
        const copy: ICourseHistory = Object.assign({}, courseHistory, {
            startdate: courseHistory.startdate != null && courseHistory.startdate.isValid() ? courseHistory.startdate.toJSON() : null,
            lastactivedate:
                courseHistory.lastactivedate != null && courseHistory.lastactivedate.isValid()
                    ? courseHistory.lastactivedate.toJSON()
                    : null
        });
        return copy;
    }

    private convertDateFromServer(res: EntityResponseType): EntityResponseType {
        res.body.startdate = res.body.startdate != null ? moment(res.body.startdate) : null;
        res.body.lastactivedate = res.body.lastactivedate != null ? moment(res.body.lastactivedate) : null;
        return res;
    }

    private convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        res.body.forEach((courseHistory: ICourseHistory) => {
            courseHistory.startdate = courseHistory.startdate != null ? moment(courseHistory.startdate) : null;
            courseHistory.lastactivedate = courseHistory.lastactivedate != null ? moment(courseHistory.lastactivedate) : null;
        });
        return res;
    }
}
