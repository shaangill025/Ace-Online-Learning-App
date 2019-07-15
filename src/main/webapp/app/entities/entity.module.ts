import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { SmartCpdCustomerModule } from './customer/customer.module';
import { SmartCpdCompanyModule } from './company/company.module';
import { SmartCpdTopicModule } from './topic/topic.module';
import { SmartCpdCourseModule } from './course/course.module';
import { SmartCpdCourseCartBridgeModule } from './course-cart-bridge/course-cart-bridge.module';
import { SmartCpdCertificateModule } from './certificate/certificate.module';
import { SmartCpdSectionModule } from './section/section.module';
import { SmartCpdQuizModule } from './quiz/quiz.module';
import { SmartCpdQuestionModule } from './question/question.module';
import { SmartCpdChoiceModule } from './choice/choice.module';
import { SmartCpdCourseHistoryModule } from './course-history/course-history.module';
import { SmartCpdSectionHistoryModule } from './section-history/section-history.module';
import { SmartCpdQuizHistoryModule } from './quiz-history/quiz-history.module';
import { SmartCpdOrdersModule } from './orders/orders.module';
import { SmartCpdCartModule } from './cart/cart.module';
import { SmartCpdTimeCourseLogModule } from './time-course-log/time-course-log.module';
import { SmartCpdServicelistModule } from './servicelist/servicelist.module';
import { SmartCpdQuizAppModule } from './quiz-app/quiz-app.module';
import { SmartCpdQuestionHistoryModule } from './question-history/question-history.module';
import { SmartCpdTagsModule } from './tags/tags.module';
import { SmartCpdBookmarkModule } from './bookmark/bookmark.module';
import { SmartCpdCheckoutModule } from './checkout/checkout.module';
import { SmartCpdCompanyRequestModule } from './company-request/company-request.module';
import { SmartCpdDashboardsModule } from './dashboards/dashboards.module';
import { SmartCpdLegacyCoursesModule } from './legacy-courses/legacy-courses.module';
import { SmartCpdMergeFunctionModule } from './merge-function/merge-function.module';
import { SmartCpdFileManagerModule } from './file-manager/file-manager.module';
import { SmartCpdTosModule } from './tos/tos.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        SmartCpdCustomerModule,
        SmartCpdCompanyModule,
        SmartCpdTopicModule,
        SmartCpdCourseModule,
        SmartCpdCourseCartBridgeModule,
        SmartCpdCertificateModule,
        SmartCpdSectionModule,
        SmartCpdQuizModule,
        SmartCpdQuestionModule,
        SmartCpdChoiceModule,
        SmartCpdCourseHistoryModule,
        SmartCpdQuestionHistoryModule,
        SmartCpdSectionHistoryModule,
        SmartCpdQuizHistoryModule,
        SmartCpdOrdersModule,
        SmartCpdCartModule,
        SmartCpdTimeCourseLogModule,
        SmartCpdServicelistModule,
        SmartCpdQuizAppModule,
        SmartCpdTagsModule,
        SmartCpdBookmarkModule,
        SmartCpdCheckoutModule,
        SmartCpdCompanyRequestModule,
        SmartCpdDashboardsModule,
        SmartCpdLegacyCoursesModule,
        SmartCpdMergeFunctionModule,
        SmartCpdFileManagerModule,
        SmartCpdTosModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdEntityModule {}
