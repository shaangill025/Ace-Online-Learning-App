import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SmartCpdSharedModule } from 'app/shared';
import {
    QuestionHistoryComponent,
    QuestionHistoryDetailComponent,
    QuestionHistoryUpdateComponent,
    QuestionHistoryDeletePopupComponent,
    QuestionHistoryDeleteDialogComponent,
    questionHistoryRoute,
    questionHistoryPopupRoute
} from './';

const ENTITY_STATES = [...questionHistoryRoute, ...questionHistoryPopupRoute];

@NgModule({
    imports: [SmartCpdSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        QuestionHistoryComponent,
        QuestionHistoryDetailComponent,
        QuestionHistoryUpdateComponent,
        QuestionHistoryDeleteDialogComponent,
        QuestionHistoryDeletePopupComponent
    ],
    entryComponents: [
        QuestionHistoryComponent,
        QuestionHistoryUpdateComponent,
        QuestionHistoryDeleteDialogComponent,
        QuestionHistoryDeletePopupComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdQuestionHistoryModule {}
