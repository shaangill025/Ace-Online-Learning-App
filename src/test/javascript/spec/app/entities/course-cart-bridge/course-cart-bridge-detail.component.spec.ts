/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SmartCpdTestModule } from '../../../test.module';
import { CourseCartBridgeDetailComponent } from 'app/entities/course-cart-bridge/course-cart-bridge-detail.component';
import { CourseCartBridge } from 'app/shared/model/course-cart-bridge.model';

describe('Component Tests', () => {
    describe('CourseCartBridge Management Detail Component', () => {
        let comp: CourseCartBridgeDetailComponent;
        let fixture: ComponentFixture<CourseCartBridgeDetailComponent>;
        const route = ({ data: of({ courseCartBridge: new CourseCartBridge(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [SmartCpdTestModule],
                declarations: [CourseCartBridgeDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(CourseCartBridgeDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(CourseCartBridgeDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.courseCartBridge).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
