import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { CourseCartBridge } from 'app/shared/model/course-cart-bridge.model';
import { CourseCartBridgeService } from './course-cart-bridge.service';
import { CourseCartBridgeComponent } from './course-cart-bridge.component';
import { CourseCartBridgeDetailComponent } from './course-cart-bridge-detail.component';
import { CourseCartBridgeUpdateComponent } from './course-cart-bridge-update.component';
import { CourseCartBridgeDeletePopupComponent } from './course-cart-bridge-delete-dialog.component';
import { ICourseCartBridge } from 'app/shared/model/course-cart-bridge.model';

@Injectable({ providedIn: 'root' })
export class CourseCartBridgeResolve implements Resolve<ICourseCartBridge> {
    constructor(private service: CourseCartBridgeService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((courseCartBridge: HttpResponse<CourseCartBridge>) => courseCartBridge.body));
        }
        return of(new CourseCartBridge());
    }
}

export const courseCartBridgeRoute: Routes = [
    {
        path: 'course-cart-bridge',
        component: CourseCartBridgeComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.courseCartBridge.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'course-cart-bridge/:id/view',
        component: CourseCartBridgeDetailComponent,
        resolve: {
            courseCartBridge: CourseCartBridgeResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.courseCartBridge.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'course-cart-bridge/new',
        component: CourseCartBridgeUpdateComponent,
        resolve: {
            courseCartBridge: CourseCartBridgeResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.courseCartBridge.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'course-cart-bridge/:id/edit',
        component: CourseCartBridgeUpdateComponent,
        resolve: {
            courseCartBridge: CourseCartBridgeResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.courseCartBridge.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const courseCartBridgePopupRoute: Routes = [
    {
        path: 'course-cart-bridge/:id/delete',
        component: CourseCartBridgeDeletePopupComponent,
        resolve: {
            courseCartBridge: CourseCartBridgeResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'smartCpdApp.courseCartBridge.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
