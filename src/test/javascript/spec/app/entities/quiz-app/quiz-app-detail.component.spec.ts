/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { QuizAppDetailComponent } from 'app/entities/quiz-app/quiz-app-detail.component';
import { QuizApp } from 'app/shared/model/quiz-app.model';

describe('Component Tests', () => {
    describe('QuizApp Management Detail Component', () => {
        let comp: QuizAppDetailComponent;
        let fixture: ComponentFixture<QuizAppDetailComponent>;
        const route = ({ data: of({ quizApp: new QuizApp(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [QuizAppDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(QuizAppDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(QuizAppDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.quizApp).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
