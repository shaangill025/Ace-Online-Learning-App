import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Quiz } from 'app/shared/model/quiz.model';
import { QuizService } from './quiz.service';
import { QuizComponent } from './quiz.component';
import { QuizDetailComponent } from './quiz-detail.component';
import { QuizUpdateComponent } from './quiz-update.component';
import { QuizDeletePopupComponent } from './quiz-delete-dialog.component';
import { IQuiz } from 'app/shared/model/quiz.model';

@Injectable({ providedIn: 'root' })
export class QuizResolve implements Resolve<IQuiz> {
    constructor(private service: QuizService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((quiz: HttpResponse<Quiz>) => quiz.body));
        }
        return of(new Quiz());
    }
}

export const quizRoute: Routes = [
    {
        path: 'quiz',
        component: QuizComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.quiz.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'quiz/:id/view',
        component: QuizDetailComponent,
        resolve: {
            quiz: QuizResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.quiz.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'quiz/new',
        component: QuizUpdateComponent,
        resolve: {
            quiz: QuizResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.quiz.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'quiz/:id/edit',
        component: QuizUpdateComponent,
        resolve: {
            quiz: QuizResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.quiz.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const quizPopupRoute: Routes = [
    {
        path: 'quiz/:id/delete',
        component: QuizDeletePopupComponent,
        resolve: {
            quiz: QuizResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.quiz.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
