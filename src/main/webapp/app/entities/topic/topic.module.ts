import {NgModule, CUSTOM_ELEMENTS_SCHEMA, Injector} from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgxSpinnerModule } from 'ngx-spinner';
import { SmartCpdSharedModule } from 'app/shared';
import {
    TopicComponent,
    TopicDetailComponent,
    TopicUpdateComponent,
    TopicDeletePopupComponent,
    TopicDeleteDialogComponent,
    topicRoute,
    topicPopupRoute
} from './';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

const ENTITY_STATES = [...topicRoute, ...topicPopupRoute];

@NgModule({
    imports: [SmartCpdSharedModule, NgxSpinnerModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [TopicComponent, TopicDetailComponent, TopicUpdateComponent, TopicDeleteDialogComponent, TopicDeletePopupComponent],
    entryComponents: [TopicComponent, TopicUpdateComponent, TopicDeleteDialogComponent, TopicDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdTopicModule {}
