import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { GooglePlacesDirective } from './google-places.directive';
import { SmartCpdSharedModule } from 'app/shared';
import { GooglePlaceModule } from 'ngx-google-places-autocomplete';

import {
    CompanyRequestComponent,
    CompanyRequestDetailComponent,
    CompanyRequestUpdateComponent,
    CompanyRequestDeletePopupComponent,
    CompanyRequestDeleteDialogComponent,
    companyRequestRoute,
    companyRequestPopupRoute
} from './';
import { FormsModule } from '@angular/forms';
import { AgmCoreModule } from '@agm/core';

const ENTITY_STATES = [...companyRequestRoute, ...companyRequestPopupRoute];

@NgModule({
    imports: [
        SmartCpdSharedModule,
        RouterModule.forChild(ENTITY_STATES),
        FormsModule,
        GooglePlaceModule,
        AgmCoreModule.forRoot({
            apiKey: 'AIzaSyCY9ksmi9JOfmLPryd1_5p5U9-tCPHXqd8',
            libraries: ['places']
        })
    ],
    declarations: [
        CompanyRequestComponent,
        CompanyRequestDetailComponent,
        CompanyRequestUpdateComponent,
        CompanyRequestDeleteDialogComponent,
        CompanyRequestDeletePopupComponent,
        GooglePlacesDirective
    ],
    entryComponents: [
        CompanyRequestComponent,
        CompanyRequestUpdateComponent,
        CompanyRequestDeleteDialogComponent,
        CompanyRequestDeletePopupComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdCompanyRequestModule {}
