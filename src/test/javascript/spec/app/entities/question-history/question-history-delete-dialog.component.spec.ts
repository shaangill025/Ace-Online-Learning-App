/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { SmartCpdTestModule } from '../../../test.module';
import { QuestionHistoryDeleteDialogComponent } from 'app/entities/question-history/question-history-delete-dialog.component';
import { QuestionHistoryService } from 'app/entities/question-history/question-history.service';

describe('Component Tests', () => {
    describe('QuestionHistory Management Delete Component', () => {
        let comp: QuestionHistoryDeleteDialogComponent;
        let fixture: ComponentFixture<QuestionHistoryDeleteDialogComponent>;
        let service: QuestionHistoryService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [QuestionHistoryDeleteDialogComponent]
            })
                .overrideTemplate(QuestionHistoryDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(QuestionHistoryDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(QuestionHistoryService);
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
