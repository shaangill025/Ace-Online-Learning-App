import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { MergeFunction } from 'app/shared/model/merge-function.model';
import { MergeFunctionService } from './merge-function.service';
import { MergeFunctionComponent } from './merge-function.component';
import { MergeFunctionDetailComponent } from './merge-function-detail.component';
import { MergeFunctionUpdateComponent } from './merge-function-update.component';
import { MergeFunctionDeletePopupComponent } from './merge-function-delete-dialog.component';
import { IMergeFunction } from 'app/shared/model/merge-function.model';

@Injectable({ providedIn: 'root' })
export class MergeFunctionResolve implements Resolve<IMergeFunction> {
    constructor(private service: MergeFunctionService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<MergeFunction> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<MergeFunction>) => response.ok),
                map((mergeFunction: HttpResponse<MergeFunction>) => mergeFunction.body)
            );
        }
        return of(new MergeFunction());
    }
}

export const mergeFunctionRoute: Routes = [
    {
        path: 'merge-function',
        component: MergeFunctionComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.mergeFunction.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'merge-function/:id/view',
        component: MergeFunctionDetailComponent,
        resolve: {
            mergeFunction: MergeFunctionResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.mergeFunction.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'merge-function/new',
        component: MergeFunctionUpdateComponent,
        resolve: {
            mergeFunction: MergeFunctionResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.mergeFunction.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'merge-function/:id/edit',
        component: MergeFunctionUpdateComponent,
        resolve: {
            mergeFunction: MergeFunctionResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.mergeFunction.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const mergeFunctionPopupRoute: Routes = [
    {
        path: 'merge-function/:id/delete',
        component: MergeFunctionDeletePopupComponent,
        resolve: {
            mergeFunction: MergeFunctionResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.mergeFunction.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
