import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IQuizApp } from 'app/shared/model/quiz-app.model';

type EntityResponseType = HttpResponse<IQuizApp>;
type EntityArrayResponseType = HttpResponse<IQuizApp[]>;

@Injectable({ providedIn: 'root' })
export class QuizAppService {
    private resourceUrl = SERVER_API_URL + 'api/quiz-apps';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/quiz-apps';

    constructor(private http: HttpClient) {}

    create(quizApp: IQuizApp): Observable<EntityResponseType> {
        return this.http.post<IQuizApp>(this.resourceUrl, quizApp, { observe: 'response' });
    }

    update(quizApp: IQuizApp): Observable<EntityResponseType> {
        return this.http.put<IQuizApp>(this.resourceUrl, quizApp, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IQuizApp>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IQuizApp[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IQuizApp[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
