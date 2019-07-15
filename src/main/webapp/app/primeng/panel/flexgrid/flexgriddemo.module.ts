import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

// import needed PrimeNG modules here

import { SmartCpdSharedModule } from '../../../shared';
import {FormsModule} from '@angular/forms';
import {CardModule} from 'primeng/primeng';
import {ButtonModule} from 'primeng/primeng';
import {GrowlModule} from 'primeng/primeng';
import {WizardModule} from 'primeng-extensions/components/wizard/wizard.js';

import {
    FlexGridDemoComponent,
    flexgridDemoRoute
} from './';

const primeng_STATES = [
    flexgridDemoRoute
];

@NgModule({
    imports: [
        SmartCpdSharedModule,
        BrowserAnimationsModule,
        FormsModule,
        CardModule,
        GrowlModule,
        ButtonModule,
        WizardModule,
        RouterModule.forRoot(primeng_STATES, { useHash: true })
    ],
    declarations: [
        FlexGridDemoComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdFlexGridDemoModule {}
