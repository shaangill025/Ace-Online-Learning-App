/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { QuizAppUpdateComponent } from 'app/entities/quiz-app/quiz-app-update.component';
import { QuizAppService } from 'app/entities/quiz-app/quiz-app.service';
import { QuizApp } from 'app/shared/model/quiz-app.model';

describe('Component Tests', () => {
    describe('QuizApp Management Update Component', () => {
        let comp: QuizAppUpdateComponent;
        let fixture: ComponentFixture<QuizAppUpdateComponent>;
        let service: QuizAppService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [QuizAppUpdateComponent]
            })
                .overrideTemplate(QuizAppUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(QuizAppUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(QuizAppService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new QuizApp(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.quizApp = entity;
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
                    const entity = new QuizApp();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.quizApp = entity;
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
