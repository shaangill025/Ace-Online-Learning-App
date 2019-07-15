/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { MergeFunctionUpdateComponent } from 'app/entities/merge-function/merge-function-update.component';
import { MergeFunctionService } from 'app/entities/merge-function/merge-function.service';
import { MergeFunction } from 'app/shared/model/merge-function.model';

describe('Component Tests', () => {
    describe('MergeFunction Management Update Component', () => {
        let comp: MergeFunctionUpdateComponent;
        let fixture: ComponentFixture<MergeFunctionUpdateComponent>;
        let service: MergeFunctionService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [MergeFunctionUpdateComponent]
            })
                .overrideTemplate(MergeFunctionUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(MergeFunctionUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(MergeFunctionService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new MergeFunction(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.mergeFunction = entity;
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
                    const entity = new MergeFunction();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.mergeFunction = entity;
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
