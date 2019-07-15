/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { SectionHistoryUpdateComponent } from 'app/entities/section-history/section-history-update.component';
import { SectionHistoryService } from 'app/entities/section-history/section-history.service';
import { SectionHistory } from 'app/shared/model/section-history.model';

describe('Component Tests', () => {
    describe('SectionHistory Management Update Component', () => {
        let comp: SectionHistoryUpdateComponent;
        let fixture: ComponentFixture<SectionHistoryUpdateComponent>;
        let service: SectionHistoryService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [SectionHistoryUpdateComponent]
            })
                .overrideTemplate(SectionHistoryUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(SectionHistoryUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SectionHistoryService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new SectionHistory(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.sectionHistory = entity;
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
                    const entity = new SectionHistory();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.sectionHistory = entity;
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
