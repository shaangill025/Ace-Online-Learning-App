/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { SmartCpdTestModule } from '../../../test.module';
import { CompanyRequestDeleteDialogComponent } from 'app/entities/company-request/company-request-delete-dialog.component';
import { CompanyRequestService } from 'app/entities/company-request/company-request.service';

describe('Component Tests', () => {
    describe('CompanyRequest Management Delete Component', () => {
        let comp: CompanyRequestDeleteDialogComponent;
        let fixture: ComponentFixture<CompanyRequestDeleteDialogComponent>;
        let service: CompanyRequestService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [CompanyRequestDeleteDialogComponent]
            })
                .overrideTemplate(CompanyRequestDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(CompanyRequestDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CompanyRequestService);
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
