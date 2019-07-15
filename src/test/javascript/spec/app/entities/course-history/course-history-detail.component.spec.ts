/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { CourseHistoryDetailComponent } from 'app/entities/course-history/course-history-detail.component';
import { CourseHistory } from 'app/shared/model/course-history.model';

describe('Component Tests', () => {
    describe('CourseHistory Management Detail Component', () => {
        let comp: CourseHistoryDetailComponent;
        let fixture: ComponentFixture<CourseHistoryDetailComponent>;
        const route = ({ data: of({ courseHistory: new CourseHistory(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [CourseHistoryDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(CourseHistoryDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(CourseHistoryDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.courseHistory).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
