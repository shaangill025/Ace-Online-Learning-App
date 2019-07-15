import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITags } from 'app/shared/model/tags.model';

@Component({
    selector: 'jhi-tags-detail',
    templateUrl: './tags-detail.component.html'
})
export class TagsDetailComponent implements OnInit {
    tags: ITags;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ tags }) => {
            this.tags = tags;
        });
    }

    previousState() {
        window.history.back();
    }
}
