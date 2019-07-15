import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SmartCpdSharedModule } from 'app/shared';
import { CheckoutComponent, checkoutRoute } from './';
import { NgxStripeModule } from 'ngx-stripe';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { NgxSpinnerModule } from 'ngx-spinner';

const ENTITY_STATES = [...checkoutRoute];

@NgModule({
    imports: [
        SmartCpdSharedModule,
        RouterModule.forChild(ENTITY_STATES),
        NgxStripeModule.forRoot('pk_test_8vSFmWGO5yHA9vaE2W5QIuvc00p94TGdTt'),
        BrowserModule,
        FormsModule,
        ReactiveFormsModule,
        NgxSpinnerModule,
        HttpClientModule
    ],
    declarations: [CheckoutComponent],
    entryComponents: [CheckoutComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdCheckoutModule {}
