import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ISection } from 'app/shared/model/section.model';
import { SectionService } from './section.service';

@Component({
    selector: 'jhi-section-delete-dialog',
    templateUrl: './section-delete-dialog.component.html'
})
export class SectionDeleteDialogComponent {
    section: ISection;

    constructor(private sectionService: SectionService, public activeModal: NgbActiveModal, private eventManager: JhiEventManager) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.sectionService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'sectionListModification',
                content: 'Deleted an section'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-section-delete-popup',
    template: ''
})
export class SectionDeletePopupComponent implements OnInit, OnDestroy {
    private ngbModalRef: NgbModalRef;

    constructor(private activatedRoute: ActivatedRoute, private router: Router, private modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ section }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(SectionDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
                this.ngbModalRef.componentInstance.section = section;
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
