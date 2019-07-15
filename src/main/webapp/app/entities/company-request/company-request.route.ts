import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { CompanyRequest } from 'app/shared/model/company-request.model';
import { CompanyRequestService } from './company-request.service';
import { CompanyRequestComponent } from './company-request.component';
import { CompanyRequestDetailComponent } from './company-request-detail.component';
import { CompanyRequestUpdateComponent } from './company-request-update.component';
import { CompanyRequestDeletePopupComponent } from './company-request-delete-dialog.component';
import { ICompanyRequest } from 'app/shared/model/company-request.model';

@Injectable({ providedIn: 'root' })
export class CompanyRequestResolve implements Resolve<ICompanyRequest> {
    constructor(private service: CompanyRequestService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<CompanyRequest> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service
                .find(id)
                .pipe(
                    filter((response: HttpResponse<CompanyRequest>) => response.ok),
                    map((companyRequest: HttpResponse<CompanyRequest>) => companyRequest.body)
                );
        }
        return of(new CompanyRequest());
    }
}

export const companyRequestRoute: Routes = [
    {
        path: 'company-request',
        component: CompanyRequestComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.companyRequest.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'company-request/:id/view',
        component: CompanyRequestDetailComponent,
        resolve: {
            companyRequest: CompanyRequestResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.companyRequest.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'company-request/new',
        component: CompanyRequestUpdateComponent,
        resolve: {
            companyRequest: CompanyRequestResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'smartCpdApp.companyRequest.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'company-request/:id/edit',
        component: CompanyRequestUpdateComponent,
        resolve: {
            companyRequest: CompanyRequestResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.companyRequest.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const companyRequestPopupRoute: Routes = [
    {
        path: 'company-request/:id/delete',
        component: CompanyRequestDeletePopupComponent,
        resolve: {
            companyRequest: CompanyRequestResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.companyRequest.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
