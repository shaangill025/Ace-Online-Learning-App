import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgxSpinnerModule } from 'ngx-spinner';
import { SmartCpdSharedModule } from 'app/shared';
import { DashboardsComponent, dashboardsRoute } from './';

const ENTITY_STATES = [...dashboardsRoute];

@NgModule({
    imports: [SmartCpdSharedModule, NgxSpinnerModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [DashboardsComponent],
    entryComponents: [DashboardsComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdDashboardsModule {}
