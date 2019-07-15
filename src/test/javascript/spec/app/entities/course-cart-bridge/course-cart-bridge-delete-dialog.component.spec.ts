/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { SmartCpdTestModule } from '../../../test.module';
import { CourseCartBridgeDeleteDialogComponent } from 'app/entities/course-cart-bridge/course-cart-bridge-delete-dialog.component';
import { CourseCartBridgeService } from 'app/entities/course-cart-bridge/course-cart-bridge.service';

describe('Component Tests', () => {
    describe('CourseCartBridge Management Delete Component', () => {
        let comp: CourseCartBridgeDeleteDialogComponent;
        let fixture: ComponentFixture<CourseCartBridgeDeleteDialogComponent>;
        let service: CourseCartBridgeService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [CourseCartBridgeDeleteDialogComponent]
            })
                .overrideTemplate(CourseCartBridgeDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(CourseCartBridgeDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CourseCartBridgeService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it(
                'Should call delete service on confirmDelete',
                inject(
                    [],
                    fakeAsync(() => {
                        // GIVEN
                        spyOn(service, 'delete').and.returnValue(of({}));

                        // WHEN
                        comp.confirmDelete(123);
                        tick();

                        // THEN
                        expect(service.delete).toHaveBeenCalledWith(123);
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                    })
                )
            );
        });
    });
});
