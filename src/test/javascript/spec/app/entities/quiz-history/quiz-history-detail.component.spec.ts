/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { QuizHistoryDetailComponent } from 'app/entities/quiz-history/quiz-history-detail.component';
import { QuizHistory } from 'app/shared/model/quiz-history.model';

describe('Component Tests', () => {
    describe('QuizHistory Management Detail Component', () => {
        let comp: QuizHistoryDetailComponent;
        let fixture: ComponentFixture<QuizHistoryDetailComponent>;
        const route = ({ data: of({ quizHistory: new QuizHistory(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [QuizHistoryDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(QuizHistoryDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(QuizHistoryDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.quizHistory).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
