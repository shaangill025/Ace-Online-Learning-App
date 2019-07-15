import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgxSpinnerModule } from 'ngx-spinner';

import { SmartCpdSharedModule } from 'app/shared';
import {
    ServicelistComponent,
    ServicelistDetailComponent,
    ServicelistUpdateComponent,
    ServicelistDeletePopupComponent,
    ServicelistDeleteDialogComponent,
    servicelistRoute,
    servicelistPopupRoute
} from './';

const ENTITY_STATES = [...servicelistRoute, ...servicelistPopupRoute];

@NgModule({
    imports: [SmartCpdSharedModule, NgxSpinnerModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        ServicelistComponent,
        ServicelistDetailComponent,
        ServicelistUpdateComponent,
        ServicelistDeleteDialogComponent,
        ServicelistDeletePopupComponent
    ],
    entryComponents: [ServicelistComponent, ServicelistUpdateComponent, ServicelistDeleteDialogComponent, ServicelistDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdServicelistModule {}
