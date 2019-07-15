import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SmartCpdSharedModule } from 'app/shared';
import {
    CourseHistoryComponent,
    CourseHistoryDetailComponent,
    CourseHistoryUpdateComponent,
    CourseHistoryDeletePopupComponent,
    CourseHistoryDeleteDialogComponent,
    courseHistoryRoute,
    courseHistoryPopupRoute
} from './';

const ENTITY_STATES = [...courseHistoryRoute, ...courseHistoryPopupRoute];

@NgModule({
    imports: [SmartCpdSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        CourseHistoryComponent,
        CourseHistoryDetailComponent,
        CourseHistoryUpdateComponent,
        CourseHistoryDeleteDialogComponent,
        CourseHistoryDeletePopupComponent
    ],
    entryComponents: [
        CourseHistoryComponent,
        CourseHistoryUpdateComponent,
        CourseHistoryDeleteDialogComponent,
        CourseHistoryDeletePopupComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdCourseHistoryModule {}
