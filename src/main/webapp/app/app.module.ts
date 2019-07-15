import './vendor.ts';

import { NgModule, Injector } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { Ng2Webstorage, LocalStorageService, SessionStorageService } from 'ngx-webstorage';
import { JhiEventManager } from 'ng-jhipster';
import { NgxSpinnerModule } from 'ngx-spinner';
import { AuthInterceptor } from './blocks/interceptor/auth.interceptor';
import { AuthExpiredInterceptor } from './blocks/interceptor/auth-expired.interceptor';
import { ErrorHandlerInterceptor } from './blocks/interceptor/errorhandler.interceptor';
import { NotificationInterceptor } from './blocks/interceptor/notification.interceptor';
import { SmartCpdSharedModule } from 'app/shared';
import { SmartCpdCoreModule } from 'app/core';
import { SmartCpdAppRoutingModule } from './app-routing.module';
import { SmartCpdHomeModule } from './home/home.module';
import { SmartCpdAccountModule } from './account/account.module';
import { SmartCpdEntityModule } from './entities/entity.module';
import { SmartCpdprimengModule } from './primeng/primeng.module';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { AdsenseModule } from 'ng2-adsense';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { JhiMainComponent, NavbarComponent, FooterComponent, PageRibbonComponent, ActiveMenuDirective, ErrorComponent } from './layouts';
import { NgxStripeModule } from 'ngx-stripe';
/*import { QuizFinalComponent } from './quiz-final/quiz-final.component';*/

@NgModule({
    imports: [
        BrowserModule,
        SmartCpdAppRoutingModule,
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-' }),
        SmartCpdSharedModule,
        SmartCpdCoreModule,
        SmartCpdHomeModule,
        SmartCpdAccountModule,
        AdsenseModule.forRoot({
            adClient: 'ca-pub-7732163028100468',
            adSlot: 7259870550
        }),
        SmartCpdEntityModule,
        SmartCpdprimengModule,
        PdfViewerModule,
        NgxSpinnerModule,
        NgxStripeModule.forRoot('pk_test_8vSFmWGO5yHA9vaE2W5QIuvc00p94TGdTt')
        // jhipster-needle-angular-add-module JHipster will add new module here
    ],
    declarations: [
        JhiMainComponent,
        NavbarComponent,
        ErrorComponent,
        PageRibbonComponent,
        ActiveMenuDirective,
        FooterComponent
        /*QuizFinalComponent*/
    ],
    providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true,
            deps: [LocalStorageService, SessionStorageService]
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthExpiredInterceptor,
            multi: true,
            deps: [Injector]
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ErrorHandlerInterceptor,
            multi: true,
            deps: [JhiEventManager]
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: NotificationInterceptor,
            multi: true,
            deps: [Injector]
        }
    ],
    bootstrap: [JhiMainComponent]
})
export class SmartCpdAppModule {}
