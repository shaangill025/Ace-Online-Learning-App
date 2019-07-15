import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IMergeFunction } from 'app/shared/model/merge-function.model';
import { MergeFunctionService } from './merge-function.service';

@Component({
    selector: 'jhi-merge-function-delete-dialog',
    templateUrl: './merge-function-delete-dialog.component.html'
})
export class MergeFunctionDeleteDialogComponent {
    mergeFunction: IMergeFunction;

    constructor(
        protected mergeFunctionService: MergeFunctionService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.mergeFunctionService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'mergeFunctionListModification',
                content: 'Deleted an mergeFunction'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-merge-function-delete-popup',
    template: ''
})
export class MergeFunctionDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ mergeFunction }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(MergeFunctionDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.mergeFunction = mergeFunction;
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
