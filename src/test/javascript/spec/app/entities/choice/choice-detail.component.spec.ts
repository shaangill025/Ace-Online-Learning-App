/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { ChoiceDetailComponent } from 'app/entities/choice/choice-detail.component';
import { Choice } from 'app/shared/model/choice.model';

describe('Component Tests', () => {
    describe('Choice Management Detail Component', () => {
        let comp: ChoiceDetailComponent;
        let fixture: ComponentFixture<ChoiceDetailComponent>;
        const route = ({ data: of({ choice: new Choice(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [ChoiceDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(ChoiceDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(ChoiceDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.choice).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
