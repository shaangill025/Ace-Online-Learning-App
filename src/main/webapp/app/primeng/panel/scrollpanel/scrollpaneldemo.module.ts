import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {CommonModule} from '@angular/common';
import { SmartCpdSharedModule } from '../../../shared';
import {ScrollPanelModule} from 'primeng/primeng';
import {CodeHighlighterModule} from 'primeng/primeng';
import {WizardModule} from 'primeng-extensions/components/wizard/wizard.js';
import { RouterModule } from '@angular/router';

import {
    ScrollPanelDemoComponent,
    scrollPanelDemoRoute
} from './';

const primeng_STATES = [
    scrollPanelDemoRoute
];

@NgModule({
    imports: [
        SmartCpdSharedModule,
        CommonModule,
        ScrollPanelModule,
        CodeHighlighterModule,
        WizardModule,
        RouterModule.forRoot(primeng_STATES, { useHash: true })
    ],
    declarations: [
        ScrollPanelDemoComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})

export class SmartCpdScrollPanelDemoModule {}
