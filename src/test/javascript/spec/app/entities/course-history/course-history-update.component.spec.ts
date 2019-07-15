/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { CourseHistoryUpdateComponent } from 'app/entities/course-history/course-history-update.component';
import { CourseHistoryService } from 'app/entities/course-history/course-history.service';
import { CourseHistory } from 'app/shared/model/course-history.model';

describe('Component Tests', () => {
    describe('CourseHistory Management Update Component', () => {
        let comp: CourseHistoryUpdateComponent;
        let fixture: ComponentFixture<CourseHistoryUpdateComponent>;
        let service: CourseHistoryService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [CourseHistoryUpdateComponent]
            })
                .overrideTemplate(CourseHistoryUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(CourseHistoryUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CourseHistoryService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new CourseHistory(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.courseHistory = entity;
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
                    const entity = new CourseHistory();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.courseHistory = entity;
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
