import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SmartCpdSharedModule } from 'app/shared';
import {
    TimeCourseLogComponent,
    TimeCourseLogDetailComponent,
    TimeCourseLogUpdateComponent,
    TimeCourseLogDeletePopupComponent,
    TimeCourseLogDeleteDialogComponent,
    timeCourseLogRoute,
    timeCourseLogPopupRoute
} from './';

const ENTITY_STATES = [...timeCourseLogRoute, ...timeCourseLogPopupRoute];

@NgModule({
    imports: [SmartCpdSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        TimeCourseLogComponent,
        TimeCourseLogDetailComponent,
        TimeCourseLogUpdateComponent,
        TimeCourseLogDeleteDialogComponent,
        TimeCourseLogDeletePopupComponent
    ],
    entryComponents: [
        TimeCourseLogComponent,
        TimeCourseLogUpdateComponent,
        TimeCourseLogDeleteDialogComponent,
        TimeCourseLogDeletePopupComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdTimeCourseLogModule {}
