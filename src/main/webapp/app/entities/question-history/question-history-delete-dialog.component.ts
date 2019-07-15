import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IQuestionHistory } from 'app/shared/model/question-history.model';
import { QuestionHistoryService } from './question-history.service';

@Component({
    selector: 'jhi-question-history-delete-dialog',
    templateUrl: './question-history-delete-dialog.component.html'
})
export class QuestionHistoryDeleteDialogComponent {
    questionHistory: IQuestionHistory;

    constructor(
        private questionHistoryService: QuestionHistoryService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.questionHistoryService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'questionHistoryListModification',
                content: 'Deleted an questionHistory'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-question-history-delete-popup',
    template: ''
})
export class QuestionHistoryDeletePopupComponent implements OnInit, OnDestroy {
    private ngbModalRef: NgbModalRef;

    constructor(private activatedRoute: ActivatedRoute, private router: Router, private modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ questionHistory }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(QuestionHistoryDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.questionHistory = questionHistory;
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
