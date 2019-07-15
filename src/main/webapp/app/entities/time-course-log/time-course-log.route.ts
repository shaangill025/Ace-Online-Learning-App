import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { TimeCourseLog } from 'app/shared/model/time-course-log.model';
import { TimeCourseLogService } from './time-course-log.service';
import { TimeCourseLogComponent } from './time-course-log.component';
import { TimeCourseLogDetailComponent } from './time-course-log-detail.component';
import { TimeCourseLogUpdateComponent } from './time-course-log-update.component';
import { TimeCourseLogDeletePopupComponent } from './time-course-log-delete-dialog.component';
import { ITimeCourseLog } from 'app/shared/model/time-course-log.model';

@Injectable({ providedIn: 'root' })
export class TimeCourseLogResolve implements Resolve<ITimeCourseLog> {
    constructor(private service: TimeCourseLogService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<TimeCourseLog> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service
                .find(id)
                .pipe(
                    filter((response: HttpResponse<TimeCourseLog>) => response.ok),
                    map((timeCourseLog: HttpResponse<TimeCourseLog>) => timeCourseLog.body)
                );
        }
        return of(new TimeCourseLog());
    }
}

export const timeCourseLogRoute: Routes = [
    {
        path: 'time-course-log',
        component: TimeCourseLogComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.timeCourseLog.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'time-course-log/:id/view',
        component: TimeCourseLogDetailComponent,
        resolve: {
            timeCourseLog: TimeCourseLogResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.timeCourseLog.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'time-course-log/new',
        component: TimeCourseLogUpdateComponent,
        resolve: {
            timeCourseLog: TimeCourseLogResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.timeCourseLog.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'time-course-log/:id/edit',
        component: TimeCourseLogUpdateComponent,
        resolve: {
            timeCourseLog: TimeCourseLogResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.timeCourseLog.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const timeCourseLogPopupRoute: Routes = [
    {
        path: 'time-course-log/:id/delete',
        component: TimeCourseLogDeletePopupComponent,
        resolve: {
            timeCourseLog: TimeCourseLogResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.timeCourseLog.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
