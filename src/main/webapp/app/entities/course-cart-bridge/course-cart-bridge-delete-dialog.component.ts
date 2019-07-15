import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ICourseCartBridge } from 'app/shared/model/course-cart-bridge.model';
import { CourseCartBridgeService } from './course-cart-bridge.service';

@Component({
    selector: 'jhi-course-cart-bridge-delete-dialog',
    templateUrl: './course-cart-bridge-delete-dialog.component.html'
})
export class CourseCartBridgeDeleteDialogComponent {
    courseCartBridge: ICourseCartBridge;

    constructor(
        private courseCartBridgeService: CourseCartBridgeService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.courseCartBridgeService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'courseCartBridgeListModification',
                content: 'Deleted an courseCartBridge'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-course-cart-bridge-delete-popup',
    template: ''
})
export class CourseCartBridgeDeletePopupComponent implements OnInit, OnDestroy {
    private ngbModalRef: NgbModalRef;

    constructor(private activatedRoute: ActivatedRoute, private router: Router, private modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ courseCartBridge }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(CourseCartBridgeDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.courseCartBridge = courseCartBridge;
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
