/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { LegacyCoursesUpdateComponent } from 'app/entities/legacy-courses/legacy-courses-update.component';
import { LegacyCoursesService } from 'app/entities/legacy-courses/legacy-courses.service';
import { LegacyCourses } from 'app/shared/model/legacy-courses.model';

describe('Component Tests', () => {
    describe('LegacyCourses Management Update Component', () => {
        let comp: LegacyCoursesUpdateComponent;
        let fixture: ComponentFixture<LegacyCoursesUpdateComponent>;
        let service: LegacyCoursesService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [LegacyCoursesUpdateComponent]
            })
                .overrideTemplate(LegacyCoursesUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(LegacyCoursesUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(LegacyCoursesService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new LegacyCourses(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.legacyCourses = entity;
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
                    const entity = new LegacyCourses();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.legacyCourses = entity;
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
