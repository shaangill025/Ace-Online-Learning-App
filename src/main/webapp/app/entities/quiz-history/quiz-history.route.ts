import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { QuizHistory } from 'app/shared/model/quiz-history.model';
import { QuizHistoryService } from './quiz-history.service';
import { QuizHistoryComponent } from './quiz-history.component';
import { QuizHistoryDetailComponent } from './quiz-history-detail.component';
import { QuizHistoryUpdateComponent } from './quiz-history-update.component';
import { QuizHistoryDeletePopupComponent } from './quiz-history-delete-dialog.component';
import { IQuizHistory } from 'app/shared/model/quiz-history.model';

@Injectable({ providedIn: 'root' })
export class QuizHistoryResolve implements Resolve<IQuizHistory> {
    constructor(private service: QuizHistoryService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((quizHistory: HttpResponse<QuizHistory>) => quizHistory.body));
        }
        return of(new QuizHistory());
    }
}

export const quizHistoryRoute: Routes = [
    {
        path: 'quiz-history',
        component: QuizHistoryComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.quizHistory.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'quiz-history/:id/view',
        component: QuizHistoryDetailComponent,
        resolve: {
            quizHistory: QuizHistoryResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.quizHistory.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'quiz-history/new',
        component: QuizHistoryUpdateComponent,
        resolve: {
            quizHistory: QuizHistoryResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.quizHistory.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'quiz-history/:id/edit',
        component: QuizHistoryUpdateComponent,
        resolve: {
            quizHistory: QuizHistoryResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.quizHistory.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const quizHistoryPopupRoute: Routes = [
    {
        path: 'quiz-history/:id/delete',
        component: QuizHistoryDeletePopupComponent,
        resolve: {
            quizHistory: QuizHistoryResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.quizHistory.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
