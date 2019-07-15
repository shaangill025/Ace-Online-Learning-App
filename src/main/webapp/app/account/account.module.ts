import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AgmCoreModule } from '@agm/core';
import { SmartCpdSharedModule } from 'app/shared';
import { FormsModule } from '@angular/forms';
import {
    PasswordStrengthBarComponent,
    RegisterComponent,
    ActivateComponent,
    PasswordComponent,
    PasswordResetInitComponent,
    PasswordResetFinishComponent,
    SettingsComponent,
    accountState
} from './';
import { GooglePlacesDirective } from './register/google-places.directive';
import { GooglePlaceModule } from 'ngx-google-places-autocomplete';

@NgModule({
    imports: [
        SmartCpdSharedModule,
        RouterModule.forChild(accountState),
        FormsModule,
        GooglePlaceModule,
        AgmCoreModule.forRoot({
            apiKey: 'AIzaSyDUG8z3_qtMxOqOPpq2T-z9xf28o82Et8c',
            libraries: ['places']
        })
    ],
    declarations: [
        ActivateComponent,
        RegisterComponent,
        PasswordComponent,
        PasswordStrengthBarComponent,
        PasswordResetInitComponent,
        PasswordResetFinishComponent,
        SettingsComponent,
        GooglePlacesDirective
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdAccountModule {}
