/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { LegacyCoursesDetailComponent } from 'app/entities/legacy-courses/legacy-courses-detail.component';
import { LegacyCourses } from 'app/shared/model/legacy-courses.model';

describe('Component Tests', () => {
    describe('LegacyCourses Management Detail Component', () => {
        let comp: LegacyCoursesDetailComponent;
        let fixture: ComponentFixture<LegacyCoursesDetailComponent>;
        const route = ({ data: of({ legacyCourses: new LegacyCourses(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [LegacyCoursesDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(LegacyCoursesDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(LegacyCoursesDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.legacyCourses).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
