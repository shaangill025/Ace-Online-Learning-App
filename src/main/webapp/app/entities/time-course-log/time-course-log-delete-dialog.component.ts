import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ITimeCourseLog } from 'app/shared/model/time-course-log.model';
import { TimeCourseLogService } from './time-course-log.service';

@Component({
    selector: 'jhi-time-course-log-delete-dialog',
    templateUrl: './time-course-log-delete-dialog.component.html'
})
export class TimeCourseLogDeleteDialogComponent {
    timeCourseLog: ITimeCourseLog;

    constructor(
        protected timeCourseLogService: TimeCourseLogService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.timeCourseLogService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'timeCourseLogListModification',
                content: 'Deleted an timeCourseLog'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-time-course-log-delete-popup',
    template: ''
})
export class TimeCourseLogDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ timeCourseLog }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(TimeCourseLogDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.timeCourseLog = timeCourseLog;
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
