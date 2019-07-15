/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { SectionHistoryDetailComponent } from 'app/entities/section-history/section-history-detail.component';
import { SectionHistory } from 'app/shared/model/section-history.model';

describe('Component Tests', () => {
    describe('SectionHistory Management Detail Component', () => {
        let comp: SectionHistoryDetailComponent;
        let fixture: ComponentFixture<SectionHistoryDetailComponent>;
        const route = ({ data: of({ sectionHistory: new SectionHistory(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [SectionHistoryDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(SectionHistoryDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(SectionHistoryDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.sectionHistory).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
