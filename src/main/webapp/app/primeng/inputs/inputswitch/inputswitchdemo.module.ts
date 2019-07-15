import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SmartCpdSharedModule } from '../../../shared';
import {FormsModule} from '@angular/forms';
import {InputSwitchModule} from 'primeng/primeng';
import {ButtonModule} from 'primeng/components/button/button';
import {GrowlModule} from 'primeng/components/growl/growl';
import {WizardModule} from 'primeng-extensions/components/wizard/wizard.js';

import {
    InputSwitchDemoComponent,
    inputswitchDemoRoute
} from './';

const primeng_STATES = [
    inputswitchDemoRoute
];

@NgModule({
    imports: [
        SmartCpdSharedModule,
        FormsModule,
        InputSwitchModule,
        GrowlModule,
        ButtonModule,
        WizardModule,
        RouterModule.forRoot(primeng_STATES, { useHash: true })
    ],
    declarations: [
        InputSwitchDemoComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdInputSwitchDemoModule {}
