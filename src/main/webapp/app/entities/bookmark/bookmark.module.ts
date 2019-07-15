import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SmartCpdSharedModule } from 'app/shared';
import {
    BookmarkComponent,
    BookmarkDetailComponent,
    BookmarkUpdateComponent,
    BookmarkDeletePopupComponent,
    BookmarkDeleteDialogComponent,
    bookmarkRoute,
    bookmarkPopupRoute
} from './';

const ENTITY_STATES = [...bookmarkRoute, ...bookmarkPopupRoute];

@NgModule({
    imports: [SmartCpdSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        BookmarkComponent,
        BookmarkDetailComponent,
        BookmarkUpdateComponent,
        BookmarkDeleteDialogComponent,
        BookmarkDeletePopupComponent
    ],
    entryComponents: [BookmarkComponent, BookmarkUpdateComponent, BookmarkDeleteDialogComponent, BookmarkDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdBookmarkModule {}
