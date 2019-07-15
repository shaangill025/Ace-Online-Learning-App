/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { FileManagerUpdateComponent } from 'app/entities/file-manager/file-manager-update.component';
import { FileManagerService } from 'app/entities/file-manager/file-manager.service';
import { FileManager } from 'app/shared/model/file-manager.model';

describe('Component Tests', () => {
    describe('FileManager Management Update Component', () => {
        let comp: FileManagerUpdateComponent;
        let fixture: ComponentFixture<FileManagerUpdateComponent>;
        let service: FileManagerService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [FileManagerUpdateComponent]
            })
                .overrideTemplate(FileManagerUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(FileManagerUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(FileManagerService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new FileManager(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.fileManager = entity;
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
                    const entity = new FileManager();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.fileManager = entity;
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
