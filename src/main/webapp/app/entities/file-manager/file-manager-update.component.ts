import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';

import { IFileManager } from 'app/shared/model/file-manager.model';
import { FileManagerService } from './file-manager.service';
import { ISection } from 'app/shared/model/section.model';
import { SectionService } from 'app/entities/section';

@Component({
    selector: 'jhi-file-manager-update',
    templateUrl: './file-manager-update.component.html'
})
export class FileManagerUpdateComponent implements OnInit {
    fileManager: IFileManager;
    isSaving: boolean;

    sections: ISection[];

    constructor(
        protected dataUtils: JhiDataUtils,
        protected jhiAlertService: JhiAlertService,
        protected fileManagerService: FileManagerService,
        protected sectionService: SectionService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ fileManager }) => {
            this.fileManager = fileManager;
        });
        this.sectionService.query().subscribe(
            (res: HttpResponse<ISection[]>) => {
                this.sections = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }

    setFileData(event, entity, field, isImage) {
        this.dataUtils.setFileData(event, entity, field, isImage);
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.fileManager.id !== undefined) {
            this.subscribeToSaveResponse(this.fileManagerService.update(this.fileManager));
        } else {
            this.subscribeToSaveResponse(this.fileManagerService.create(this.fileManager));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IFileManager>>) {
        result.subscribe((res: HttpResponse<IFileManager>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackSectionById(index: number, item: ISection) {
        return item.id;
    }
}
