/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { MergeFunctionDetailComponent } from 'app/entities/merge-function/merge-function-detail.component';
import { MergeFunction } from 'app/shared/model/merge-function.model';

describe('Component Tests', () => {
    describe('MergeFunction Management Detail Component', () => {
        let comp: MergeFunctionDetailComponent;
        let fixture: ComponentFixture<MergeFunctionDetailComponent>;
        const route = ({ data: of({ mergeFunction: new MergeFunction(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [MergeFunctionDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(MergeFunctionDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(MergeFunctionDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.mergeFunction).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
