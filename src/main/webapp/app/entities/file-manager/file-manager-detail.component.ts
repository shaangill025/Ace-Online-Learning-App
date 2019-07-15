import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IFileManager } from 'app/shared/model/file-manager.model';

@Component({
    selector: 'jhi-file-manager-detail',
    templateUrl: './file-manager-detail.component.html'
})
export class FileManagerDetailComponent implements OnInit {
    fileManager: IFileManager;

    constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ fileManager }) => {
            this.fileManager = fileManager;
        });
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }
    previousState() {
        window.history.back();
    }
}
