import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IQuestionHistory } from 'app/shared/model/question-history.model';

type EntityResponseType = HttpResponse<IQuestionHistory>;
type EntityArrayResponseType = HttpResponse<IQuestionHistory[]>;

@Injectable({ providedIn: 'root' })
export class QuestionHistoryService {
    private resourceUrl = SERVER_API_URL + 'api/question-histories';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/question-histories';

    constructor(private http: HttpClient) {}

    create(questionHistory: IQuestionHistory): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(questionHistory);
        return this.http
            .post<IQuestionHistory>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(questionHistory: IQuestionHistory): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(questionHistory);
        return this.http
            .put<IQuestionHistory>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<IQuestionHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IQuestionHistory[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IQuestionHistory[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    private convertDateFromClient(questionHistory: IQuestionHistory): IQuestionHistory {
        const copy: IQuestionHistory = Object.assign({}, questionHistory, {
            timestamp: questionHistory.timestamp != null && questionHistory.timestamp.isValid() ? questionHistory.timestamp.toJSON() : null
        });
        return copy;
    }

    private convertDateFromServer(res: EntityResponseType): EntityResponseType {
        res.body.timestamp = res.body.timestamp != null ? moment(res.body.timestamp) : null;
        return res;
    }

    private convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        res.body.forEach((questionHistory: IQuestionHistory) => {
            questionHistory.timestamp = questionHistory.timestamp != null ? moment(questionHistory.timestamp) : null;
        });
        return res;
    }
}
