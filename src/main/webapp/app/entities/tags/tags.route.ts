import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Tags } from 'app/shared/model/tags.model';
import { TagsService } from './tags.service';
import { TagsComponent } from './tags.component';
import { TagsDetailComponent } from './tags-detail.component';
import { TagsUpdateComponent } from './tags-update.component';
import { TagsDeletePopupComponent } from './tags-delete-dialog.component';
import { ITags } from 'app/shared/model/tags.model';

@Injectable({ providedIn: 'root' })
export class TagsResolve implements Resolve<ITags> {
    constructor(private service: TagsService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((tags: HttpResponse<Tags>) => tags.body));
        }
        return of(new Tags());
    }
}

export const tagsRoute: Routes = [
    {
        path: 'tags',
        component: TagsComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.tags.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'tags/:id/view',
        component: TagsDetailComponent,
        resolve: {
            tags: TagsResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.tags.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'tags/new',
        component: TagsUpdateComponent,
        resolve: {
            tags: TagsResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.tags.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'tags/:id/edit',
        component: TagsUpdateComponent,
        resolve: {
            tags: TagsResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.tags.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const tagsPopupRoute: Routes = [
    {
        path: 'tags/:id/delete',
        component: TagsDeletePopupComponent,
        resolve: {
            tags: TagsResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.tags.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
