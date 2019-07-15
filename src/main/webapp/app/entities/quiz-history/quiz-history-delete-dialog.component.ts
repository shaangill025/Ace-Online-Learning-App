import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IQuizHistory } from 'app/shared/model/quiz-history.model';
import { QuizHistoryService } from './quiz-history.service';

@Component({
    selector: 'jhi-quiz-history-delete-dialog',
    templateUrl: './quiz-history-delete-dialog.component.html'
})
export class QuizHistoryDeleteDialogComponent {
    quizHistory: IQuizHistory;

    constructor(
        private quizHistoryService: QuizHistoryService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.quizHistoryService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'quizHistoryListModification',
                content: 'Deleted an quizHistory'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-quiz-history-delete-popup',
    template: ''
})
export class QuizHistoryDeletePopupComponent implements OnInit, OnDestroy {
    private ngbModalRef: NgbModalRef;

    constructor(private activatedRoute: ActivatedRoute, private router: Router, private modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ quizHistory }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(QuizHistoryDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.quizHistory = quizHistory;
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
