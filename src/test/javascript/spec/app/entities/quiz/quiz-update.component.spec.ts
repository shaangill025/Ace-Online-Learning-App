/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { QuizUpdateComponent } from 'app/entities/quiz/quiz-update.component';
import { QuizService } from 'app/entities/quiz/quiz.service';
import { Quiz } from 'app/shared/model/quiz.model';

describe('Component Tests', () => {
    describe('Quiz Management Update Component', () => {
        let comp: QuizUpdateComponent;
        let fixture: ComponentFixture<QuizUpdateComponent>;
        let service: QuizService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [QuizUpdateComponent]
            })
                .overrideTemplate(QuizUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(QuizUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(QuizService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Quiz(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.quiz = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.update).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );

            it(
                'Should call create service on save for new entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Quiz();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.quiz = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.create).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );
        });
    });
});
