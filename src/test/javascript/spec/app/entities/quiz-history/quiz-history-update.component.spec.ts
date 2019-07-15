/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { QuizHistoryUpdateComponent } from 'app/entities/quiz-history/quiz-history-update.component';
import { QuizHistoryService } from 'app/entities/quiz-history/quiz-history.service';
import { QuizHistory } from 'app/shared/model/quiz-history.model';

describe('Component Tests', () => {
    describe('QuizHistory Management Update Component', () => {
        let comp: QuizHistoryUpdateComponent;
        let fixture: ComponentFixture<QuizHistoryUpdateComponent>;
        let service: QuizHistoryService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [QuizHistoryUpdateComponent]
            })
                .overrideTemplate(QuizHistoryUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(QuizHistoryUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(QuizHistoryService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new QuizHistory(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.quizHistory = entity;
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
                    const entity = new QuizHistory();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.quizHistory = entity;
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
