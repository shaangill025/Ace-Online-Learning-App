import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Servicelist } from 'app/shared/model/servicelist.model';
import { ServicelistService } from './servicelist.service';
import { ServicelistComponent } from './servicelist.component';
import { ServicelistDetailComponent } from './servicelist-detail.component';
import { ServicelistUpdateComponent } from './servicelist-update.component';
import { ServicelistDeletePopupComponent } from './servicelist-delete-dialog.component';
import { IServicelist } from 'app/shared/model/servicelist.model';

@Injectable({ providedIn: 'root' })
export class ServicelistResolve implements Resolve<IServicelist> {
    constructor(private service: ServicelistService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((servicelist: HttpResponse<Servicelist>) => servicelist.body));
        }
        return of(new Servicelist());
    }
}

export const servicelistRoute: Routes = [
    {
        path: 'servicelist',
        component: ServicelistComponent,
        data: {
            authorities: ['ROLE_ADMIN', 'ROLE_USER'],
            pageTitle: 'smartCpdApp.servicelist.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'servicelist/:id/view',
        component: ServicelistDetailComponent,
        resolve: {
            servicelist: ServicelistResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.servicelist.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'servicelist/new',
        component: ServicelistUpdateComponent,
        resolve: {
            servicelist: ServicelistResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.servicelist.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'servicelist/:id/edit',
        component: ServicelistUpdateComponent,
        resolve: {
            servicelist: ServicelistResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.servicelist.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const servicelistPopupRoute: Routes = [
    {
        path: 'servicelist/:id/delete',
        component: ServicelistDeletePopupComponent,
        resolve: {
            servicelist: ServicelistResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.servicelist.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
