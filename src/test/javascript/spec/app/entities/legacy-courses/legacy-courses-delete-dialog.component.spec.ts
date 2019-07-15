/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { SmartCpdTestModule } from '../../../test.module';
import { LegacyCoursesDeleteDialogComponent } from 'app/entities/legacy-courses/legacy-courses-delete-dialog.component';
import { LegacyCoursesService } from 'app/entities/legacy-courses/legacy-courses.service';

describe('Component Tests', () => {
    describe('LegacyCourses Management Delete Component', () => {
        let comp: LegacyCoursesDeleteDialogComponent;
        let fixture: ComponentFixture<LegacyCoursesDeleteDialogComponent>;
        let service: LegacyCoursesService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [LegacyCoursesDeleteDialogComponent]
            })
                .overrideTemplate(LegacyCoursesDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(LegacyCoursesDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(LegacyCoursesService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
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
            ));
        });
    });
});
