/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { SmartCpdTestModule } from '../../../test.module';
import { CourseHistoryDeleteDialogComponent } from 'app/entities/course-history/course-history-delete-dialog.component';
import { CourseHistoryService } from 'app/entities/course-history/course-history.service';

describe('Component Tests', () => {
    describe('CourseHistory Management Delete Component', () => {
        let comp: CourseHistoryDeleteDialogComponent;
        let fixture: ComponentFixture<CourseHistoryDeleteDialogComponent>;
        let service: CourseHistoryService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [CourseHistoryDeleteDialogComponent]
            })
                .overrideTemplate(CourseHistoryDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(CourseHistoryDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CourseHistoryService);
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
