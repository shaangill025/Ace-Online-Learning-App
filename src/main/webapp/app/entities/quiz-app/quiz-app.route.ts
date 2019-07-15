import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { QuizApp } from 'app/shared/model/quiz-app.model';
import { QuizAppService } from './quiz-app.service';
import { QuizAppComponent } from './quiz-app.component';
import { QuizAppDetailComponent } from './quiz-app-detail.component';
import { QuizAppUpdateComponent } from './quiz-app-update.component';
import { QuizAppDeletePopupComponent } from './quiz-app-delete-dialog.component';
import { IQuizApp } from 'app/shared/model/quiz-app.model';

@Injectable({ providedIn: 'root' })
export class QuizAppResolve implements Resolve<IQuizApp> {
    constructor(private service: QuizAppService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((quizApp: HttpResponse<QuizApp>) => quizApp.body));
        }
        return of(new QuizApp());
    }
}

export const quizAppRoute: Routes = [
    {
        path: 'quiz-app',
        component: QuizAppComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.quizApp.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'quiz-app/:id/view',
        component: QuizAppDetailComponent,
        resolve: {
            quizApp: QuizAppResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'smartCpdApp.quizApp.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'quiz-app/new',
        component: QuizAppUpdateComponent,
        resolve: {
            quizApp: QuizAppResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.quizApp.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'quiz-app/:id/edit',
        component: QuizAppUpdateComponent,
        resolve: {
            quizApp: QuizAppResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.quizApp.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const quizAppPopupRoute: Routes = [
    {
        path: 'quiz-app/:id/delete',
        component: QuizAppDeletePopupComponent,
        resolve: {
            quizApp: QuizAppResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.quizApp.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
