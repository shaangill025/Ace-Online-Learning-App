import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { LegacyCourses } from 'app/shared/model/legacy-courses.model';
import { LegacyCoursesService } from './legacy-courses.service';
import { LegacyCoursesComponent } from './legacy-courses.component';
import { LegacyCoursesDetailComponent } from './legacy-courses-detail.component';
import { LegacyCoursesUpdateComponent } from './legacy-courses-update.component';
import { LegacyCoursesDeletePopupComponent } from './legacy-courses-delete-dialog.component';
import { ILegacyCourses } from 'app/shared/model/legacy-courses.model';

@Injectable({ providedIn: 'root' })
export class LegacyCoursesResolve implements Resolve<ILegacyCourses> {
    constructor(private service: LegacyCoursesService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<LegacyCourses> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<LegacyCourses>) => response.ok),
                map((legacyCourses: HttpResponse<LegacyCourses>) => legacyCourses.body)
            );
        }
        return of(new LegacyCourses());
    }
}

export const legacyCoursesRoute: Routes = [
    {
        path: 'legacy-courses',
        component: LegacyCoursesComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.legacyCourses.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'legacy-courses/:id/view',
        component: LegacyCoursesDetailComponent,
        resolve: {
            legacyCourses: LegacyCoursesResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.legacyCourses.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'legacy-courses/new',
        component: LegacyCoursesUpdateComponent,
        resolve: {
            legacyCourses: LegacyCoursesResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.legacyCourses.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'legacy-courses/:id/edit',
        component: LegacyCoursesUpdateComponent,
        resolve: {
            legacyCourses: LegacyCoursesResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.legacyCourses.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const legacyCoursesPopupRoute: Routes = [
    {
        path: 'legacy-courses/:id/delete',
        component: LegacyCoursesDeletePopupComponent,
        resolve: {
            legacyCourses: LegacyCoursesResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.legacyCourses.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
