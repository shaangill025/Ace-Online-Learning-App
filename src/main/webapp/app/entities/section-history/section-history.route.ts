import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { SectionHistory } from 'app/shared/model/section-history.model';
import { SectionHistoryService } from './section-history.service';
import { SectionHistoryComponent } from './section-history.component';
import { SectionHistoryDetailComponent } from './section-history-detail.component';
import { SectionHistoryUpdateComponent } from './section-history-update.component';
import { SectionHistoryDeletePopupComponent } from './section-history-delete-dialog.component';
import { ISectionHistory } from 'app/shared/model/section-history.model';

@Injectable({ providedIn: 'root' })
export class SectionHistoryResolve implements Resolve<ISectionHistory> {
    constructor(private service: SectionHistoryService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((sectionHistory: HttpResponse<SectionHistory>) => sectionHistory.body));
        }
        return of(new SectionHistory());
    }
}

export const sectionHistoryRoute: Routes = [
    {
        path: 'section-history',
        component: SectionHistoryComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.sectionHistory.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'section-history/:id/view',
        component: SectionHistoryDetailComponent,
        resolve: {
            sectionHistory: SectionHistoryResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.sectionHistory.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'section-history/new',
        component: SectionHistoryUpdateComponent,
        resolve: {
            sectionHistory: SectionHistoryResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.sectionHistory.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'section-history/:id/edit',
        component: SectionHistoryUpdateComponent,
        resolve: {
            sectionHistory: SectionHistoryResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.sectionHistory.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const sectionHistoryPopupRoute: Routes = [
    {
        path: 'section-history/:id/delete',
        component: SectionHistoryDeletePopupComponent,
        resolve: {
            sectionHistory: SectionHistoryResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.sectionHistory.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
