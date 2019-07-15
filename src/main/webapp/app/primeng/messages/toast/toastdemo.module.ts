import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SmartCpdSharedModule } from '../../../shared';
import {ButtonModule} from 'primeng/primeng';
import {ToastModule} from 'primeng/toast';
import {GrowlModule} from 'primeng/primeng';
import {WizardModule} from 'primeng-extensions/components/wizard/wizard.js';

import {
    ToastDemoComponent,
    toastDemoRoute
} from './';

const primeng_STATES = [
    toastDemoRoute
];

@NgModule({
    imports: [
        SmartCpdSharedModule,
        ButtonModule,
        ToastModule,
        GrowlModule,
        WizardModule,
        RouterModule.forRoot(primeng_STATES, { useHash: true })
    ],
    declarations: [
        ToastDemoComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdToastDemoModule {}
