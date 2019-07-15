/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { SmartCpdTestModule } from '../../../test.module';
import { MergeFunctionDeleteDialogComponent } from 'app/entities/merge-function/merge-function-delete-dialog.component';
import { MergeFunctionService } from 'app/entities/merge-function/merge-function.service';

describe('Component Tests', () => {
    describe('MergeFunction Management Delete Component', () => {
        let comp: MergeFunctionDeleteDialogComponent;
        let fixture: ComponentFixture<MergeFunctionDeleteDialogComponent>;
        let service: MergeFunctionService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [MergeFunctionDeleteDialogComponent]
            })
                .overrideTemplate(MergeFunctionDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(MergeFunctionDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(MergeFunctionService);
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
