/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { CompanyRequestDetailComponent } from 'app/entities/company-request/company-request-detail.component';
import { CompanyRequest } from 'app/shared/model/company-request.model';

describe('Component Tests', () => {
    describe('CompanyRequest Management Detail Component', () => {
        let comp: CompanyRequestDetailComponent;
        let fixture: ComponentFixture<CompanyRequestDetailComponent>;
        const route = ({ data: of({ companyRequest: new CompanyRequest(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [CompanyRequestDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(CompanyRequestDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(CompanyRequestDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.companyRequest).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
