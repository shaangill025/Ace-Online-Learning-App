import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ICourseCartBridge } from 'app/shared/model/course-cart-bridge.model';

type EntityResponseType = HttpResponse<ICourseCartBridge>;
type EntityArrayResponseType = HttpResponse<ICourseCartBridge[]>;

@Injectable({ providedIn: 'root' })
export class CourseCartBridgeService {
    private resourceUrl = SERVER_API_URL + 'api/course-cart-bridges';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/course-cart-bridges';
    private resourceCollectionUrl = SERVER_API_URL + 'api/collection/course-cart-bridges';
    private resourceCountUrl = SERVER_API_URL + 'api/count/course/course-cart-bridges';
    private resourceInstancesUrl = SERVER_API_URL + 'api/instances/course-cart-bridges';

    constructor(private http: HttpClient) {}

    create(courseCartBridge: ICourseCartBridge): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(courseCartBridge);
        return this.http
            .post<ICourseCartBridge>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    getcollection(id: number): Observable<ICourseCartBridge[]> {
        return this.http.get<ICourseCartBridge[]>(`${this.resourceCollectionUrl}/${id}`);
    }

    getcount(id: number): Observable<number> {
        return this.http.get<number>(`${this.resourceCountUrl}/${id}`);
    }

    getinstance(courseid: number, cartid: number): Observable<ICourseCartBridge> {
        return this.http.post<ICourseCartBridge>(this.resourceInstancesUrl, { cartId: cartid, courseId: courseid });
    }

    update(courseCartBridge: ICourseCartBridge): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(courseCartBridge);
        return this.http
            .put<ICourseCartBridge>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<ICourseCartBridge>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<ICourseCartBridge[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<ICourseCartBridge[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    private convertDateFromClient(courseCartBridge: ICourseCartBridge): ICourseCartBridge {
        const copy: ICourseCartBridge = Object.assign({}, courseCartBridge, {
            timestamp:
                courseCartBridge.timestamp != null && courseCartBridge.timestamp.isValid() ? courseCartBridge.timestamp.toJSON() : null
        });
        return copy;
    }

    private convertDateFromServer(res: EntityResponseType): EntityResponseType {
        res.body.timestamp = res.body.timestamp != null ? moment(res.body.timestamp) : null;
        return res;
    }

    private convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        res.body.forEach((courseCartBridge: ICourseCartBridge) => {
            courseCartBridge.timestamp = courseCartBridge.timestamp != null ? moment(courseCartBridge.timestamp) : null;
        });
        return res;
    }
}
