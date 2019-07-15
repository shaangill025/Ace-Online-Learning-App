import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ISectionHistory } from 'app/shared/model/section-history.model';
import { SectionHistoryService } from './section-history.service';

@Component({
    selector: 'jhi-section-history-delete-dialog',
    templateUrl: './section-history-delete-dialog.component.html'
})
export class SectionHistoryDeleteDialogComponent {
    sectionHistory: ISectionHistory;

    constructor(
        private sectionHistoryService: SectionHistoryService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.sectionHistoryService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'sectionHistoryListModification',
                content: 'Deleted an sectionHistory'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-section-history-delete-popup',
    template: ''
})
export class SectionHistoryDeletePopupComponent implements OnInit, OnDestroy {
    private ngbModalRef: NgbModalRef;

    constructor(private activatedRoute: ActivatedRoute, private router: Router, private modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ sectionHistory }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(SectionHistoryDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.sectionHistory = sectionHistory;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true, queryParamsHandling: 'merge' });
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true, queryParamsHandling: 'merge' });
                        this.ngbModalRef = null;
                    }
                );
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }
}
