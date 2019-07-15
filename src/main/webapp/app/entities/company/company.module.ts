import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { GooglePlaceModule } from 'ngx-google-places-autocomplete';
import { SmartCpdSharedModule } from 'app/shared';
import { NgxSpinnerModule } from 'ngx-spinner';
import {
    CompanyComponent,
    CompanyDetailComponent,
    CompanyUpdateComponent,
    CompanyDeletePopupComponent,
    CompanyDeleteDialogComponent,
    companyRoute,
    companyPopupRoute
} from './';
import { FormsModule } from '@angular/forms';
import { AgmCoreModule } from '@agm/core';
import { GooglePlacesDirective } from './google-places.directive';

const ENTITY_STATES = [...companyRoute, ...companyPopupRoute];

@NgModule({
    imports: [
        SmartCpdSharedModule,
        RouterModule.forChild(ENTITY_STATES),
        FormsModule,
        NgxSpinnerModule,
        GooglePlaceModule,
        AgmCoreModule.forRoot({
            apiKey: 'AIzaSyCY9ksmi9JOfmLPryd1_5p5U9-tCPHXqd8',
            libraries: ['places']
        })
    ],
    declarations: [
        CompanyComponent,
        CompanyDetailComponent,
        CompanyUpdateComponent,
        CompanyDeleteDialogComponent,
        CompanyDeletePopupComponent,
        GooglePlacesDirective
    ],
    entryComponents: [CompanyComponent, CompanyUpdateComponent, CompanyDeleteDialogComponent, CompanyDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdCompanyModule {}
