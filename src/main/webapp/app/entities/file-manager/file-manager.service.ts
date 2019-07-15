import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IFileManager } from 'app/shared/model/file-manager.model';

type EntityResponseType = HttpResponse<IFileManager>;
type EntityArrayResponseType = HttpResponse<IFileManager[]>;

@Injectable({ providedIn: 'root' })
export class FileManagerService {
    public resourceUrl = SERVER_API_URL + 'api/file-managers';
    public resourceFileUrl = SERVER_API_URL + 'api/file-managers/file';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/file-managers';

    constructor(protected http: HttpClient) {}

    create(fileManager: IFileManager): Observable<EntityResponseType> {
        return this.http.post<IFileManager>(this.resourceUrl, fileManager, { observe: 'response' });
    }

    update(fileManager: IFileManager): Observable<EntityResponseType> {
        return this.http.put<IFileManager>(this.resourceUrl, fileManager, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IFileManager>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    getFile(id: number): Observable<Int8Array> {
        return this.http.get<Int8Array>(`${this.resourceFileUrl}/${id}`);
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IFileManager[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IFileManager[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
