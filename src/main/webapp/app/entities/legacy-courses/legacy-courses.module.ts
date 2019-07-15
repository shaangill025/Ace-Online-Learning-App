import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SmartCpdSharedModule } from 'app/shared';
import {
    LegacyCoursesComponent,
    LegacyCoursesDetailComponent,
    LegacyCoursesUpdateComponent,
    LegacyCoursesDeletePopupComponent,
    LegacyCoursesDeleteDialogComponent,
    legacyCoursesRoute,
    legacyCoursesPopupRoute
} from './';

const ENTITY_STATES = [...legacyCoursesRoute, ...legacyCoursesPopupRoute];

@NgModule({
    imports: [SmartCpdSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        LegacyCoursesComponent,
        LegacyCoursesDetailComponent,
        LegacyCoursesUpdateComponent,
        LegacyCoursesDeleteDialogComponent,
        LegacyCoursesDeletePopupComponent
    ],
    entryComponents: [
        LegacyCoursesComponent,
        LegacyCoursesUpdateComponent,
        LegacyCoursesDeleteDialogComponent,
        LegacyCoursesDeletePopupComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdLegacyCoursesModule {}
