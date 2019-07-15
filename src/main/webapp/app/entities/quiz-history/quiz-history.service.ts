import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IQuizHistory } from 'app/shared/model/quiz-history.model';

type EntityResponseType = HttpResponse<IQuizHistory>;
type EntityArrayResponseType = HttpResponse<IQuizHistory[]>;

@Injectable({ providedIn: 'root' })
export class QuizHistoryService {
    private resourceUrl = SERVER_API_URL + 'api/quiz-histories';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/quiz-histories';

    constructor(private http: HttpClient) {}

    create(quizHistory: IQuizHistory): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(quizHistory);
        return this.http
            .post<IQuizHistory>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(quizHistory: IQuizHistory): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(quizHistory);
        return this.http
            .put<IQuizHistory>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<IQuizHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    lastattmpt(sectionid: number, customerid: number): Observable<boolean> {
        return this.http
        .get<boolean>(`${SERVER_API_URL + 'api/attempt'}/${sectionid}/${'watched/quiz-history'}/${customerid}`);
    }
    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IQuizHistory[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IQuizHistory[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    private convertDateFromClient(quizHistory: IQuizHistory): IQuizHistory {
        const copy: IQuizHistory = Object.assign({}, quizHistory, {
            start: quizHistory.start != null && quizHistory.start.isValid() ? quizHistory.start.toJSON() : null
        });
        return copy;
    }

    private convertDateFromServer(res: EntityResponseType): EntityResponseType {
        res.body.start = res.body.start != null ? moment(res.body.start) : null;
        return res;
    }

    private convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        res.body.forEach((quizHistory: IQuizHistory) => {
            quizHistory.start = quizHistory.start != null ? moment(quizHistory.start) : null;
        });
        return res;
    }
}
