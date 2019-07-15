import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Choice } from 'app/shared/model/choice.model';
import { ChoiceService } from './choice.service';
import { ChoiceComponent } from './choice.component';
import { ChoiceDetailComponent } from './choice-detail.component';
import { ChoiceUpdateComponent } from './choice-update.component';
import { ChoiceDeletePopupComponent } from './choice-delete-dialog.component';
import { IChoice } from 'app/shared/model/choice.model';

@Injectable({ providedIn: 'root' })
export class ChoiceResolve implements Resolve<IChoice> {
    constructor(private service: ChoiceService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((choice: HttpResponse<Choice>) => choice.body));
        }
        return of(new Choice());
    }
}

export const choiceRoute: Routes = [
    {
        path: 'choice',
        component: ChoiceComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.choice.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'choice/:id/view',
        component: ChoiceDetailComponent,
        resolve: {
            choice: ChoiceResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.choice.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'choice/new',
        component: ChoiceUpdateComponent,
        resolve: {
            choice: ChoiceResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.choice.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'choice/:id/edit',
        component: ChoiceUpdateComponent,
        resolve: {
            choice: ChoiceResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.choice.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const choicePopupRoute: Routes = [
    {
        path: 'choice/:id/delete',
        component: ChoiceDeletePopupComponent,
        resolve: {
            choice: ChoiceResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.choice.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
