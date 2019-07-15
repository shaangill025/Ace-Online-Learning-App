import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SmartCpdSharedModule } from '../../../shared';
import {FormsModule} from '@angular/forms';
import {CheckboxModule} from 'primeng/components/checkbox/checkbox';
import {TriStateCheckboxModule} from 'primeng/components/tristatecheckbox/tristatecheckbox';
import {GrowlModule} from 'primeng/components/growl/growl';
import {WizardModule} from 'primeng-extensions/components/wizard/wizard.js';

import {
    CheckboxDemoComponent,
    checkboxDemoRoute
} from './';

const primeng_STATES = [
    checkboxDemoRoute
];

@NgModule({
    imports: [
        SmartCpdSharedModule,
        FormsModule,
        CheckboxModule,
        GrowlModule,
        TriStateCheckboxModule,
        WizardModule,
        RouterModule.forRoot(primeng_STATES, { useHash: true })
    ],
    declarations: [
        CheckboxDemoComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdCheckboxDemoModule {}
