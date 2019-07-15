import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { CourseHistory } from 'app/shared/model/course-history.model';
import { CourseHistoryService } from './course-history.service';
import { CourseHistoryComponent } from './course-history.component';
import { CourseHistoryDetailComponent } from './course-history-detail.component';
import { CourseHistoryUpdateComponent } from './course-history-update.component';
import { CourseHistoryDeletePopupComponent } from './course-history-delete-dialog.component';
import { ICourseHistory } from 'app/shared/model/course-history.model';

@Injectable({ providedIn: 'root' })
export class CourseHistoryResolve implements Resolve<ICourseHistory> {
    constructor(private service: CourseHistoryService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((courseHistory: HttpResponse<CourseHistory>) => courseHistory.body));
        }
        return of(new CourseHistory());
    }
}

export const courseHistoryRoute: Routes = [
    {
        path: 'course-history',
        component: CourseHistoryComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.courseHistory.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'course-history/:id/view',
        component: CourseHistoryDetailComponent,
        resolve: {
            courseHistory: CourseHistoryResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.courseHistory.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'course-history/new',
        component: CourseHistoryUpdateComponent,
        resolve: {
            courseHistory: CourseHistoryResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.courseHistory.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'course-history/:id/edit',
        component: CourseHistoryUpdateComponent,
        resolve: {
            courseHistory: CourseHistoryResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.courseHistory.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const courseHistoryPopupRoute: Routes = [
    {
        path: 'course-history/:id/delete',
        component: CourseHistoryDeletePopupComponent,
        resolve: {
            courseHistory: CourseHistoryResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.courseHistory.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
