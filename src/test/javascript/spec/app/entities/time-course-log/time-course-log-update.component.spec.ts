/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { TimeCourseLogUpdateComponent } from 'app/entities/time-course-log/time-course-log-update.component';
import { TimeCourseLogService } from 'app/entities/time-course-log/time-course-log.service';
import { TimeCourseLog } from 'app/shared/model/time-course-log.model';

describe('Component Tests', () => {
    describe('TimeCourseLog Management Update Component', () => {
        let comp: TimeCourseLogUpdateComponent;
        let fixture: ComponentFixture<TimeCourseLogUpdateComponent>;
        let service: TimeCourseLogService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [TimeCourseLogUpdateComponent]
            })
                .overrideTemplate(TimeCourseLogUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(TimeCourseLogUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(TimeCourseLogService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new TimeCourseLog(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.timeCourseLog = entity;
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
                    const entity = new TimeCourseLog();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.timeCourseLog = entity;
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
