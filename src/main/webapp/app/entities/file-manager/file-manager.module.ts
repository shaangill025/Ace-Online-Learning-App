import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SmartCpdSharedModule } from 'app/shared';
import {
    FileManagerComponent,
    FileManagerDetailComponent,
    FileManagerUpdateComponent,
    FileManagerDeletePopupComponent,
    FileManagerDeleteDialogComponent,
    fileManagerRoute,
    fileManagerPopupRoute
} from './';

const ENTITY_STATES = [...fileManagerRoute, ...fileManagerPopupRoute];

@NgModule({
    imports: [SmartCpdSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        FileManagerComponent,
        FileManagerDetailComponent,
        FileManagerUpdateComponent,
        FileManagerDeleteDialogComponent,
        FileManagerDeletePopupComponent
    ],
    entryComponents: [FileManagerComponent, FileManagerUpdateComponent, FileManagerDeleteDialogComponent, FileManagerDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdFileManagerModule {}
