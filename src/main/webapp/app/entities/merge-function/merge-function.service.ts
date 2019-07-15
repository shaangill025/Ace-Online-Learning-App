import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IMergeFunction } from 'app/shared/model/merge-function.model';

type EntityResponseType = HttpResponse<IMergeFunction>;
type EntityArrayResponseType = HttpResponse<IMergeFunction[]>;

@Injectable({ providedIn: 'root' })
export class MergeFunctionService {
    public resourceUrl = SERVER_API_URL + 'api/merge-functions';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/merge-functions';

    constructor(protected http: HttpClient) {}

    create(mergeFunction: IMergeFunction): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(mergeFunction);
        return this.http
            .post<IMergeFunction>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(mergeFunction: IMergeFunction): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(mergeFunction);
        return this.http
            .put<IMergeFunction>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<IMergeFunction>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IMergeFunction[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IMergeFunction[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    protected convertDateFromClient(mergeFunction: IMergeFunction): IMergeFunction {
        const copy: IMergeFunction = Object.assign({}, mergeFunction, {
            datetime: mergeFunction.datetime != null && mergeFunction.datetime.isValid() ? mergeFunction.datetime.format(DATE_FORMAT) : null
        });
        return copy;
    }

    protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
        if (res.body) {
            res.body.datetime = res.body.datetime != null ? moment(res.body.datetime) : null;
        }
        return res;
    }

    protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        if (res.body) {
            res.body.forEach((mergeFunction: IMergeFunction) => {
                mergeFunction.datetime = mergeFunction.datetime != null ? moment(mergeFunction.datetime) : null;
            });
        }
        return res;
    }
}
