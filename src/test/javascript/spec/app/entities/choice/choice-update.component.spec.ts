/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { ChoiceUpdateComponent } from 'app/entities/choice/choice-update.component';
import { ChoiceService } from 'app/entities/choice/choice.service';
import { Choice } from 'app/shared/model/choice.model';

describe('Component Tests', () => {
    describe('Choice Management Update Component', () => {
        let comp: ChoiceUpdateComponent;
        let fixture: ComponentFixture<ChoiceUpdateComponent>;
        let service: ChoiceService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [ChoiceUpdateComponent]
            })
                .overrideTemplate(ChoiceUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(ChoiceUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ChoiceService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Choice(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.choice = entity;
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
                    const entity = new Choice();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.choice = entity;
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
