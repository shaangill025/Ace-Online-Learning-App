import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { ILegacyCourses } from 'app/shared/model/legacy-courses.model';

@Component({
    selector: 'jhi-legacy-courses-detail',
    templateUrl: './legacy-courses-detail.component.html'
})
export class LegacyCoursesDetailComponent implements OnInit {
    legacyCourses: ILegacyCourses;

    constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ legacyCourses }) => {
            this.legacyCourses = legacyCourses;
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
