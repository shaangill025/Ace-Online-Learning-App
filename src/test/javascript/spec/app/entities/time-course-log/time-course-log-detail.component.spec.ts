/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { TimeCourseLogDetailComponent } from 'app/entities/time-course-log/time-course-log-detail.component';
import { TimeCourseLog } from 'app/shared/model/time-course-log.model';

describe('Component Tests', () => {
    describe('TimeCourseLog Management Detail Component', () => {
        let comp: TimeCourseLogDetailComponent;
        let fixture: ComponentFixture<TimeCourseLogDetailComponent>;
        const route = ({ data: of({ timeCourseLog: new TimeCourseLog(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [TimeCourseLogDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(TimeCourseLogDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(TimeCourseLogDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.timeCourseLog).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
