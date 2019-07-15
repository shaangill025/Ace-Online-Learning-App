import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AdsenseModule } from 'ng2-adsense';

import { SmartCpdSharedModule } from 'app/shared';
import { HOME_ROUTE, HomeComponent } from './';

@NgModule({
    imports: [
        SmartCpdSharedModule,
        AdsenseModule.forRoot({
            adClient: 'ca-pub-7732163028100468',
            adSlot: 7259870550
        }),
        RouterModule.forChild([HOME_ROUTE])
    ],
    declarations: [HomeComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdHomeModule {}
