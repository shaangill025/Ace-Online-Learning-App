/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { ServicelistUpdateComponent } from 'app/entities/servicelist/servicelist-update.component';
import { ServicelistService } from 'app/entities/servicelist/servicelist.service';
import { Servicelist } from 'app/shared/model/servicelist.model';

describe('Component Tests', () => {
    describe('Servicelist Management Update Component', () => {
        let comp: ServicelistUpdateComponent;
        let fixture: ComponentFixture<ServicelistUpdateComponent>;
        let service: ServicelistService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [ServicelistUpdateComponent]
            })
                .overrideTemplate(ServicelistUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(ServicelistUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ServicelistService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Servicelist(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.servicelist = entity;
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
                    const entity = new Servicelist();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.servicelist = entity;
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
