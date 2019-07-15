/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { CourseCartBridgeUpdateComponent } from 'app/entities/course-cart-bridge/course-cart-bridge-update.component';
import { CourseCartBridgeService } from 'app/entities/course-cart-bridge/course-cart-bridge.service';
import { CourseCartBridge } from 'app/shared/model/course-cart-bridge.model';

describe('Component Tests', () => {
    describe('CourseCartBridge Management Update Component', () => {
        let comp: CourseCartBridgeUpdateComponent;
        let fixture: ComponentFixture<CourseCartBridgeUpdateComponent>;
        let service: CourseCartBridgeService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [CourseCartBridgeUpdateComponent]
            })
                .overrideTemplate(CourseCartBridgeUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(CourseCartBridgeUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CourseCartBridgeService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new CourseCartBridge(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.courseCartBridge = entity;
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
                    const entity = new CourseCartBridge();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.courseCartBridge = entity;
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
