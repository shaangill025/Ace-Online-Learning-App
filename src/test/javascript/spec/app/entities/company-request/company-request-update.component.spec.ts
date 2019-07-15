/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { CompanyRequestUpdateComponent } from 'app/entities/company-request/company-request-update.component';
import { CompanyRequestService } from 'app/entities/company-request/company-request.service';
import { CompanyRequest } from 'app/shared/model/company-request.model';

describe('Component Tests', () => {
    describe('CompanyRequest Management Update Component', () => {
        let comp: CompanyRequestUpdateComponent;
        let fixture: ComponentFixture<CompanyRequestUpdateComponent>;
        let service: CompanyRequestService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [CompanyRequestUpdateComponent]
            })
                .overrideTemplate(CompanyRequestUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(CompanyRequestUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CompanyRequestService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new CompanyRequest(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.companyRequest = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.update).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );

            it(
                'Should call create service on save for new entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new CompanyRequest();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.companyRequest = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.create).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );
        });
    });
});
