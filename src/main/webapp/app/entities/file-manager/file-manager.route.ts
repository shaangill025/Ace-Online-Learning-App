import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { FileManager } from 'app/shared/model/file-manager.model';
import { FileManagerService } from './file-manager.service';
import { FileManagerComponent } from './file-manager.component';
import { FileManagerDetailComponent } from './file-manager-detail.component';
import { FileManagerUpdateComponent } from './file-manager-update.component';
import { FileManagerDeletePopupComponent } from './file-manager-delete-dialog.component';
import { IFileManager } from 'app/shared/model/file-manager.model';

@Injectable({ providedIn: 'root' })
export class FileManagerResolve implements Resolve<IFileManager> {
    constructor(private service: FileManagerService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<FileManager> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<FileManager>) => response.ok),
                map((fileManager: HttpResponse<FileManager>) => fileManager.body)
            );
        }
        return of(new FileManager());
    }
}

export const fileManagerRoute: Routes = [
    {
        path: 'file-manager',
        component: FileManagerComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'smartCpdApp.fileManager.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'file-manager/:id/view',
        component: FileManagerDetailComponent,
        resolve: {
            fileManager: FileManagerResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'smartCpdApp.fileManager.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'file-manager/new',
        component: FileManagerUpdateComponent,
        resolve: {
            fileManager: FileManagerResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'smartCpdApp.fileManager.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'file-manager/:id/edit',
        component: FileManagerUpdateComponent,
        resolve: {
            fileManager: FileManagerResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'smartCpdApp.fileManager.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const fileManagerPopupRoute: Routes = [
    {
        path: 'file-manager/:id/delete',
        component: FileManagerDeletePopupComponent,
        resolve: {
            fileManager: FileManagerResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'smartCpdApp.fileManager.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
