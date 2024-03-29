import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Section } from 'app/shared/model/section.model';
import { SectionService } from './section.service';
import { SectionComponent } from './section.component';
import { SectionDetailComponent } from './section-detail.component';
import { SectionUpdateComponent } from './section-update.component';
import { SectionDeletePopupComponent } from './section-delete-dialog.component';
import { ISection } from 'app/shared/model/section.model';

@Injectable({ providedIn: 'root' })
export class SectionResolve implements Resolve<ISection> {
    constructor(private service: SectionService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((section: HttpResponse<Section>) => section.body));
        }
        return of(new Section());
    }
}

export const sectionRoute: Routes = [
    {
        path: 'section',
        component: SectionComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.section.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'section/:id/view',
        component: SectionDetailComponent,
        resolve: {
            section: SectionResolve
        },
        data: {
            authorities: ['ROLE_ADMIN', 'ROLE_USER'],
            pageTitle: 'smartCpdApp.section.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'section/new',
        component: SectionUpdateComponent,
        resolve: {
            section: SectionResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.section.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'section/:id/edit',
        component: SectionUpdateComponent,
        resolve: {
            section: SectionResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.section.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const sectionPopupRoute: Routes = [
    {
        path: 'section/:id/delete',
        component: SectionDeletePopupComponent,
        resolve: {
            section: SectionResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.section.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
