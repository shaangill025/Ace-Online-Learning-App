/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { SmartCpdTestModule } from '../../../test.module';
import { TosComponent } from 'app/entities/tos/tos.component';
import { TosService } from 'app/entities/tos/tos.service';
import { Tos } from 'app/shared/model/tos.model';

describe('Component Tests', () => {
    describe('Tos Management Component', () => {
        let comp: TosComponent;
        let fixture: ComponentFixture<TosComponent>;
        let service: TosService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [TosComponent],
                providers: []
            })
                .overrideTemplate(TosComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(TosComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(TosService);
        });

        it('Should call load all on init', () => {
            // GIVEN
            const headers = new HttpHeaders().append('link', 'link;link');
            spyOn(service, 'query').and.returnValue(
                of(
                    new HttpResponse({
                        body: [new Tos(123)],
                        headers
                    })
                )
            );

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.query).toHaveBeenCalled();
            expect(comp.tos[0]).toEqual(jasmine.objectContaining({ id: 123 }));
        });
    });
});
