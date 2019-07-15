import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ILegacyCourses } from 'app/shared/model/legacy-courses.model';
import { LegacyCoursesService } from './legacy-courses.service';

@Component({
    selector: 'jhi-legacy-courses-delete-dialog',
    templateUrl: './legacy-courses-delete-dialog.component.html'
})
export class LegacyCoursesDeleteDialogComponent {
    legacyCourses: ILegacyCourses;

    constructor(
        protected legacyCoursesService: LegacyCoursesService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.legacyCoursesService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'legacyCoursesListModification',
                content: 'Deleted an legacyCourses'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-legacy-courses-delete-popup',
    template: ''
})
export class LegacyCoursesDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ legacyCourses }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(LegacyCoursesDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.legacyCourses = legacyCourses;
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
