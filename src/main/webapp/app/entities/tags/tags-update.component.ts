import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ITags } from 'app/shared/model/tags.model';
import { TagsService } from './tags.service';

@Component({
    selector: 'jhi-tags-update',
    templateUrl: './tags-update.component.html'
})
export class TagsUpdateComponent implements OnInit {
    private _tags: ITags;
    isSaving: boolean;

    constructor(private tagsService: TagsService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ tags }) => {
            this.tags = tags;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.tags.id !== undefined) {
            this.subscribeToSaveResponse(this.tagsService.update(this.tags));
        } else {
            this.subscribeToSaveResponse(this.tagsService.create(this.tags));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ITags>>) {
        result.subscribe((res: HttpResponse<ITags>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
    get tags() {
        return this._tags;
    }

    set tags(tags: ITags) {
        this._tags = tags;
    }
}
