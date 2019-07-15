import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IServicelist } from 'app/shared/model/servicelist.model';
import { ServicelistService } from './servicelist.service';

@Component({
    selector: 'jhi-servicelist-delete-dialog',
    templateUrl: './servicelist-delete-dialog.component.html'
})
export class ServicelistDeleteDialogComponent {
    servicelist: IServicelist;

    constructor(
        private servicelistService: ServicelistService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.servicelistService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'servicelistListModification',
                content: 'Deleted an servicelist'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-servicelist-delete-popup',
    template: ''
})
export class ServicelistDeletePopupComponent implements OnInit, OnDestroy {
    private ngbModalRef: NgbModalRef;

    constructor(private activatedRoute: ActivatedRoute, private router: Router, private modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ servicelist }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(ServicelistDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.servicelist = servicelist;
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
