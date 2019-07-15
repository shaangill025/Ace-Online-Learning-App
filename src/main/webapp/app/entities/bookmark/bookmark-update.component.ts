import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';

import { IBookmark } from 'app/shared/model/bookmark.model';
import { BookmarkService } from './bookmark.service';
import { ISection } from 'app/shared/model/section.model';
import { SectionService } from 'app/entities/section';

@Component({
    selector: 'jhi-bookmark-update',
    templateUrl: './bookmark-update.component.html'
})
export class BookmarkUpdateComponent implements OnInit {
    private _bookmark: IBookmark;
    isSaving: boolean;

    sections: ISection[];

    constructor(
        private jhiAlertService: JhiAlertService,
        private bookmarkService: BookmarkService,
        private sectionService: SectionService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ bookmark }) => {
            this.bookmark = bookmark;
        });
        this.sectionService.query().subscribe(
            (res: HttpResponse<ISection[]>) => {
                this.sections = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    getSeconds(time: string) {
        const hrs = time.substring(0, 2);
        const min = time.substring(3, 5);
        const sec = time.substring(6, 8);
        const hrsNum = Number(hrs);
        const minNum = Number(min);
        const secNum = Number(sec);
        return hrsNum * 3600 + minNum * 60 + secNum;
    }

    save() {
        this.isSaving = true;
        if (this.bookmark.seconds != null) {
            this.bookmark.seconds = this.getSeconds(this.bookmark.timestamp);
        }
        if (this.bookmark.id !== undefined) {
            this.subscribeToSaveResponse(this.bookmarkService.update(this.bookmark));
        } else {
            this.subscribeToSaveResponse(this.bookmarkService.create(this.bookmark));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IBookmark>>) {
        result.subscribe((res: HttpResponse<IBookmark>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackSectionById(index: number, item: ISection) {
        return item.id;
    }
    get bookmark() {
        return this._bookmark;
    }

    set bookmark(bookmark: IBookmark) {
        this._bookmark = bookmark;
    }
}
