/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { SmartCpdTestModule } from '../../../test.module';
import { BookmarkDeleteDialogComponent } from 'app/entities/bookmark/bookmark-delete-dialog.component';
import { BookmarkService } from 'app/entities/bookmark/bookmark.service';

describe('Component Tests', () => {
    describe('Bookmark Management Delete Component', () => {
        let comp: BookmarkDeleteDialogComponent;
        let fixture: ComponentFixture<BookmarkDeleteDialogComponent>;
        let service: BookmarkService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [BookmarkDeleteDialogComponent]
            })
                .overrideTemplate(BookmarkDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(BookmarkDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(BookmarkService);
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
