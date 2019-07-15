/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { SmartCpdTestModule } from '../../../test.module';
import { TimeCourseLogDeleteDialogComponent } from 'app/entities/time-course-log/time-course-log-delete-dialog.component';
import { TimeCourseLogService } from 'app/entities/time-course-log/time-course-log.service';

describe('Component Tests', () => {
    describe('TimeCourseLog Management Delete Component', () => {
        let comp: TimeCourseLogDeleteDialogComponent;
        let fixture: ComponentFixture<TimeCourseLogDeleteDialogComponent>;
        let service: TimeCourseLogService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [TimeCourseLogDeleteDialogComponent]
            })
                .overrideTemplate(TimeCourseLogDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(TimeCourseLogDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(TimeCourseLogService);
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
