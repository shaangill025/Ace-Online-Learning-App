import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiDataUtils } from 'ng-jhipster';

import { ITopic } from 'app/shared/model/topic.model';
import { TopicService } from './topic.service';

@Component({
    selector: 'jhi-topic-update',
    templateUrl: './topic-update.component.html'
})
export class TopicUpdateComponent implements OnInit {
    private _topic: ITopic;
    isSaving: boolean;

    constructor(private dataUtils: JhiDataUtils, private topicService: TopicService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ topic }) => {
            this.topic = topic;
        });
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
        if (this.topic.id !== undefined) {
            this.subscribeToSaveResponse(this.topicService.update(this.topic));
        } else {
            this.subscribeToSaveResponse(this.topicService.create(this.topic));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ITopic>>) {
        result.subscribe((res: HttpResponse<ITopic>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
    get topic() {
        return this._topic;
    }

    set topic(topic: ITopic) {
        this._topic = topic;
    }
}
