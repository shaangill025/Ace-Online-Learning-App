import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IQuizApp } from 'app/shared/model/quiz-app.model';
import { QuizAppService } from './quiz-app.service';

@Component({
    selector: 'jhi-quiz-app-delete-dialog',
    templateUrl: './quiz-app-delete-dialog.component.html'
})
export class QuizAppDeleteDialogComponent {
    quizApp: IQuizApp;

    constructor(private quizAppService: QuizAppService, public activeModal: NgbActiveModal, private eventManager: JhiEventManager) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.quizAppService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'quizAppListModification',
                content: 'Deleted an quizApp'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-quiz-app-delete-popup',
    template: ''
})
export class QuizAppDeletePopupComponent implements OnInit, OnDestroy {
    private ngbModalRef: NgbModalRef;

    constructor(private activatedRoute: ActivatedRoute, private router: Router, private modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ quizApp }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(QuizAppDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
                this.ngbModalRef.componentInstance.quizApp = quizApp;
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
