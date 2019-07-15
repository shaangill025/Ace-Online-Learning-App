/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { QuestionHistoryDetailComponent } from 'app/entities/question-history/question-history-detail.component';
import { QuestionHistory } from 'app/shared/model/question-history.model';

describe('Component Tests', () => {
    describe('QuestionHistory Management Detail Component', () => {
        let comp: QuestionHistoryDetailComponent;
        let fixture: ComponentFixture<QuestionHistoryDetailComponent>;
        const route = ({ data: of({ questionHistory: new QuestionHistory(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [QuestionHistoryDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(QuestionHistoryDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(QuestionHistoryDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.questionHistory).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
