import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { EditorModule } from 'primeng/components/editor/editor';
import { ButtonModule } from 'primeng/components/button/button';
import { GrowlModule } from 'primeng/components/growl/growl';
import { WizardModule } from 'primeng-extensions/components/wizard/wizard.js';
import { SharedModule } from 'primeng/components/common/shared';
import { SmartCpdSharedModule } from 'app/shared';
import { ProgressBarModule } from 'primeng/components/progressbar/progressbar';
import { NgxSpinnerModule } from 'ngx-spinner';
import {
    QuizAppComponent,
    QuizAppDetailComponent,
    QuizAppUpdateComponent,
    QuizAppDeletePopupComponent,
    QuizAppDeleteDialogComponent,
    quizAppRoute,
    quizAppPopupRoute
} from './';
import {HTTP_INTERCEPTORS} from "@angular/common/http";

const ENTITY_STATES = [...quizAppRoute, ...quizAppPopupRoute];

@NgModule({
    imports: [
        SmartCpdSharedModule,
        RouterModule.forChild(ENTITY_STATES),
        EditorModule,
        ButtonModule,
        GrowlModule,
        WizardModule,
        SharedModule,
        ProgressBarModule,
        NgxSpinnerModule
    ],
    declarations: [
        QuizAppComponent,
        QuizAppDetailComponent,
        QuizAppUpdateComponent,
        QuizAppDeleteDialogComponent,
        QuizAppDeletePopupComponent
    ],
    entryComponents: [QuizAppComponent, QuizAppUpdateComponent, QuizAppDeleteDialogComponent, QuizAppDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdQuizAppModule {}
