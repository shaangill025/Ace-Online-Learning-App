/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { FileManagerDetailComponent } from 'app/entities/file-manager/file-manager-detail.component';
import { FileManager } from 'app/shared/model/file-manager.model';

describe('Component Tests', () => {
    describe('FileManager Management Detail Component', () => {
        let comp: FileManagerDetailComponent;
        let fixture: ComponentFixture<FileManagerDetailComponent>;
        const route = ({ data: of({ fileManager: new FileManager(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [FileManagerDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(FileManagerDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(FileManagerDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.fileManager).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
