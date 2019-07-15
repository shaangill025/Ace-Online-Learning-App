import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ICompanyRequest } from 'app/shared/model/company-request.model';
import { CompanyRequestService } from './company-request.service';

@Component({
    selector: 'jhi-company-request-delete-dialog',
    templateUrl: './company-request-delete-dialog.component.html'
})
export class CompanyRequestDeleteDialogComponent {
    companyRequest: ICompanyRequest;

    constructor(
        protected companyRequestService: CompanyRequestService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.companyRequestService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'companyRequestListModification',
                content: 'Deleted an companyRequest'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-company-request-delete-popup',
    template: ''
})
export class CompanyRequestDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ companyRequest }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(CompanyRequestDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.companyRequest = companyRequest;
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
