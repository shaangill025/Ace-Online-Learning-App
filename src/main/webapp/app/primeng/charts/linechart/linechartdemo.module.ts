import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SmartCpdSharedModule } from '../../../shared';
import { ChartModule } from 'primeng/primeng';

import {
    LinechartDemoComponent,
    linechartDemoRoute
} from '../../charts/linechart';

const primeng_STATES = [
    linechartDemoRoute
];

@NgModule({
    imports: [
        SmartCpdSharedModule,
        ChartModule,
        RouterModule.forRoot(primeng_STATES, { useHash: true })
    ],
    declarations: [
        LinechartDemoComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdLinechartDemoModule {}
