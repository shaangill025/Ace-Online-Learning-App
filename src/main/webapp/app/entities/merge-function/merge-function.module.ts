import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SmartCpdSharedModule } from 'app/shared';
import {
    MergeFunctionComponent,
    MergeFunctionDetailComponent,
    MergeFunctionUpdateComponent,
    MergeFunctionDeletePopupComponent,
    MergeFunctionDeleteDialogComponent,
    mergeFunctionRoute,
    mergeFunctionPopupRoute
} from './';

const ENTITY_STATES = [...mergeFunctionRoute, ...mergeFunctionPopupRoute];

@NgModule({
    imports: [SmartCpdSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        MergeFunctionComponent,
        MergeFunctionDetailComponent,
        MergeFunctionUpdateComponent,
        MergeFunctionDeleteDialogComponent,
        MergeFunctionDeletePopupComponent
    ],
    entryComponents: [
        MergeFunctionComponent,
        MergeFunctionUpdateComponent,
        MergeFunctionDeleteDialogComponent,
        MergeFunctionDeletePopupComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdMergeFunctionModule {}
