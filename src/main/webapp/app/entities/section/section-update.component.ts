import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';

import { ISection } from 'app/shared/model/section.model';
import { SectionService } from './section.service';
import { IQuiz } from 'app/shared/model/quiz.model';
import { QuizService } from 'app/entities/quiz';
import { ITags } from 'app/shared/model/tags.model';
import { TagsService } from 'app/entities/tags';
import { ICourse } from 'app/shared/model/course.model';
import { CourseService } from 'app/entities/course';

@Component({
    selector: 'jhi-section-update',
    templateUrl: './section-update.component.html'
})
export class SectionUpdateComponent implements OnInit {
    private _section: ISection;
    isSaving: boolean;

    quizzes: IQuiz[];

    tags: ITags[];

    courses: ICourse[];

    constructor(
        private dataUtils: JhiDataUtils,
        private jhiAlertService: JhiAlertService,
        private sectionService: SectionService,
        private quizService: QuizService,
        private tagsService: TagsService,
        private courseService: CourseService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ section }) => {
            this.section = section;
        });
        this.quizService.query({ filter: 'section-is-null' }).subscribe(
            (res: HttpResponse<IQuiz[]>) => {
                if (!this.section.quiz || !this.section.quiz.id) {
                    this.quizzes = res.body;
                } else {
                    this.quizService.find(this.section.quiz.id).subscribe(
                        (subRes: HttpResponse<IQuiz>) => {
                            this.quizzes = [subRes.body].concat(res.body);
                        },
                        (subRes: HttpErrorResponse) => this.onError(subRes.message)
                    );
                }
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.tagsService.query().subscribe(
            (res: HttpResponse<ITags[]>) => {
                this.tags = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.courseService.query().subscribe(
            (res: HttpResponse<ICourse[]>) => {
                this.courses = res.body;
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
        if (this.section.id !== undefined) {
            this.subscribeToSaveResponse(this.sectionService.update(this.section));
        } else {
            this.subscribeToSaveResponse(this.sectionService.create(this.section));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ISection>>) {
        result.subscribe((res: HttpResponse<ISection>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

    trackQuizById(index: number, item: IQuiz) {
        return item.id;
    }

    trackTagsById(index: number, item: ITags) {
        return item.id;
    }

    trackCourseById(index: number, item: ICourse) {
        return item.id;
    }

    getSelected(selectedVals: Array<any>, option: any) {
        if (selectedVals) {
            for (let i = 0; i < selectedVals.length; i++) {
                if (option.id === selectedVals[i].id) {
                    return selectedVals[i];
                }
            }
        }
        return option;
    }
    get section() {
        return this._section;
    }

    set section(section: ISection) {
        this._section = section;
    }
}
