import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { ITopic } from 'app/shared/model/topic.model';

@Component({
    selector: 'jhi-topic-detail',
    templateUrl: './topic-detail.component.html'
})
export class TopicDetailComponent implements OnInit {
    topic: ITopic;

    constructor(private dataUtils: JhiDataUtils, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
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
    previousState() {
        window.history.back();
    }
}
