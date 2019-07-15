import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Checkout } from 'app/shared/model/checkout.model';
import { CheckoutComponent } from './checkout.component';
import { ICheckout } from 'app/shared/model/checkout.model';

export const checkoutRoute: Routes = [
    {
        path: 'checkout',
        component: CheckoutComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'smartCpdApp.checkout.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];
