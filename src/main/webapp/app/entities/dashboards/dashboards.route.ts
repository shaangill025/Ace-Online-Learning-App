import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Dashboards } from 'app/shared/model/dashboards.model';
import { DashboardsService } from './dashboards.service';
import { DashboardsComponent } from './dashboards.component';
import { IDashboards } from 'app/shared/model/dashboards.model';

@Injectable({ providedIn: 'root' })
export class DashboardsResolve implements Resolve<IDashboards> {
    constructor(private service: DashboardsService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((dashboards: HttpResponse<Dashboards>) => dashboards.body));
        }
        return of(new Dashboards());
    }
}

export const dashboardsRoute: Routes = [
    {
        path: 'dashboards',
        component: DashboardsComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'smartCpdApp.dashboards.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];
