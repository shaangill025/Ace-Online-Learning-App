import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SmartCpdSharedModule } from 'app/shared';
import {
    TagsComponent,
    TagsDetailComponent,
    TagsUpdateComponent,
    TagsDeletePopupComponent,
    TagsDeleteDialogComponent,
    tagsRoute,
    tagsPopupRoute
} from './';

const ENTITY_STATES = [...tagsRoute, ...tagsPopupRoute];

@NgModule({
    imports: [SmartCpdSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [TagsComponent, TagsDetailComponent, TagsUpdateComponent, TagsDeleteDialogComponent, TagsDeletePopupComponent],
    entryComponents: [TagsComponent, TagsUpdateComponent, TagsDeleteDialogComponent, TagsDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdTagsModule {}
