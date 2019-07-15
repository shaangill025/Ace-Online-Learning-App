import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SmartCpdSharedModule } from 'app/shared';
import { TosComponent, tosRoute } from './';

const ENTITY_STATES = [...tosRoute];

@NgModule({
    imports: [SmartCpdSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [TosComponent],
    entryComponents: [TosComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdTosModule {}
