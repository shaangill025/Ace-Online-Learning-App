import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { QuestionHistory } from 'app/shared/model/question-history.model';
import { QuestionHistoryService } from './question-history.service';
import { QuestionHistoryComponent } from './question-history.component';
import { QuestionHistoryDetailComponent } from './question-history-detail.component';
import { QuestionHistoryUpdateComponent } from './question-history-update.component';
import { QuestionHistoryDeletePopupComponent } from './question-history-delete-dialog.component';
import { IQuestionHistory } from 'app/shared/model/question-history.model';

@Injectable({ providedIn: 'root' })
export class QuestionHistoryResolve implements Resolve<IQuestionHistory> {
    constructor(private service: QuestionHistoryService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((questionHistory: HttpResponse<QuestionHistory>) => questionHistory.body));
        }
        return of(new QuestionHistory());
    }
}

export const questionHistoryRoute: Routes = [
    {
        path: 'question-history',
        component: QuestionHistoryComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.questionHistory.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'question-history/:id/view',
        component: QuestionHistoryDetailComponent,
        resolve: {
            questionHistory: QuestionHistoryResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.questionHistory.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'question-history/new',
        component: QuestionHistoryUpdateComponent,
        resolve: {
            questionHistory: QuestionHistoryResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.questionHistory.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'question-history/:id/edit',
        component: QuestionHistoryUpdateComponent,
        resolve: {
            questionHistory: QuestionHistoryResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.questionHistory.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const questionHistoryPopupRoute: Routes = [
    {
        path: 'question-history/:id/delete',
        component: QuestionHistoryDeletePopupComponent,
        resolve: {
            questionHistory: QuestionHistoryResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.questionHistory.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
