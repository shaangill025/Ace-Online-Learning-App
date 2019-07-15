/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { ServicelistDetailComponent } from 'app/entities/servicelist/servicelist-detail.component';
import { Servicelist } from 'app/shared/model/servicelist.model';

describe('Component Tests', () => {
    describe('Servicelist Management Detail Component', () => {
        let comp: ServicelistDetailComponent;
        let fixture: ComponentFixture<ServicelistDetailComponent>;
        const route = ({ data: of({ servicelist: new Servicelist(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [ServicelistDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(ServicelistDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(ServicelistDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.servicelist).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
