import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Topic } from 'app/shared/model/topic.model';
import { TopicService } from './topic.service';
import { TopicComponent } from './topic.component';
import { TopicDetailComponent } from './topic-detail.component';
import { TopicUpdateComponent } from './topic-update.component';
import { TopicDeletePopupComponent } from './topic-delete-dialog.component';
import { ITopic } from 'app/shared/model/topic.model';

@Injectable({ providedIn: 'root' })
export class TopicResolve implements Resolve<ITopic> {
    constructor(private service: TopicService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((topic: HttpResponse<Topic>) => topic.body));
        }
        return of(new Topic());
    }
}

export const topicRoute: Routes = [
    {
        path: 'topic',
        component: TopicComponent,
        data: {
            authorities: [],
            pageTitle: 'smartCpdApp.topic.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'topic/:id/view',
        component: TopicDetailComponent,
        resolve: {
            topic: TopicResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'smartCpdApp.topic.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'topic/new',
        component: TopicUpdateComponent,
        resolve: {
            topic: TopicResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.topic.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'topic/:id/edit',
        component: TopicUpdateComponent,
        resolve: {
            topic: TopicResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.topic.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const topicPopupRoute: Routes = [
    {
        path: 'topic/:id/delete',
        component: TopicDeletePopupComponent,
        resolve: {
            topic: TopicResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.topic.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
