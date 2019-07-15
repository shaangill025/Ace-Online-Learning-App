import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgxSpinnerModule } from 'ngx-spinner';

import { SmartCpdSharedModule } from 'app/shared';
import {
    CertificateComponent,
    CertificateDetailComponent,
    CertificateUpdateComponent,
    CertificateDeletePopupComponent,
    CertificateDeleteDialogComponent,
    certificateRoute,
    certificatePopupRoute
} from './';

const ENTITY_STATES = [...certificateRoute, ...certificatePopupRoute];

@NgModule({
    imports: [SmartCpdSharedModule, NgxSpinnerModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        CertificateComponent,
        CertificateDetailComponent,
        CertificateUpdateComponent,
        CertificateDeleteDialogComponent,
        CertificateDeletePopupComponent
    ],
    entryComponents: [CertificateComponent, CertificateUpdateComponent, CertificateDeleteDialogComponent, CertificateDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdCertificateModule {}
