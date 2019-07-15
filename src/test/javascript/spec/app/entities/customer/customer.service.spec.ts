/* tslint:disable max-line-length */
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { take, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { CustomerService } from 'app/entities/customer/customer.service';
import { ICustomer, Customer, TYPES } from 'app/shared/model/customer.model';

describe('Service Tests', () => {
    describe('Customer Service', () => {
        let injector: TestBed;
        let service: CustomerService;
        let httpMock: HttpTestingController;
        let elemDefault: ICustomer;
        let currentDate: moment.Moment;
        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule]
            });
            injector = getTestBed();
            service = injector.get(CustomerService);
            httpMock = injector.get(HttpTestingController);
            currentDate = moment();

            elemDefault = new Customer(
                0,
                'AAAAAAA',
                'AAAAAAA',
                'AAAAAAA',
                'AAAAAAA',
                'AAAAAAA',
                'AAAAAAA',
                'AAAAAAA',
                currentDate,
                currentDate,
                0,
                'AAAAAAA',
                TYPES.RESIDENCE,
                'AAAAAAA',
                'AAAAAAA',
                false,
                'AAAAAAA',
                0,
                'AAAAAAA'
            );
        });

        describe('Service methods', async () => {
            it('should find an element', async () => {
                const returnedFromService = Object.assign(
                    {
                        registered: currentDate.format(DATE_TIME_FORMAT),
                        lastactive: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );
                service
                    .find(123)
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: elemDefault }));

                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should create a Customer', async () => {
                const returnedFromService = Object.assign(
                    {
                        id: 0,
                        registered: currentDate.format(DATE_TIME_FORMAT),
                        lastactive: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );
                const expected = Object.assign(
                    {
                        registered: currentDate,
                        lastactive: currentDate
                    },
                    returnedFromService
                );
                service
                    .create(new Customer(null))
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: expected }));
                const req = httpMock.expectOne({ method: 'POST' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should update a Customer', async () => {
                const returnedFromService = Object.assign(
                    {
                        normalized: 'BBBBBB',
                        phone: 'BBBBBB',
                        streetaddress: 'BBBBBB',
                        postalcode: 'BBBBBB',
                        city: 'BBBBBB',
                        stateProvince: 'BBBBBB',
                        country: 'BBBBBB',
                        registered: currentDate.format(DATE_TIME_FORMAT),
                        lastactive: currentDate.format(DATE_TIME_FORMAT),
                        points: 1,
                        areaserviced: 'BBBBBB',
                        specialities: 'BBBBBB',
                        trades: 'BBBBBB',
                        monthYear: 'BBBBBB',
                        show: true,
                        hidden: 'BBBBBB',
                        licenceCycle: 1,
                        licenceNumber: 'BBBBBB'
                    },
                    elemDefault
                );

                const expected = Object.assign(
                    {
                        registered: currentDate,
                        lastactive: currentDate
                    },
                    returnedFromService
                );
                service
                    .update(expected)
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: expected }));
                const req = httpMock.expectOne({ method: 'PUT' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should return a list of Customer', async () => {
                const returnedFromService = Object.assign(
                    {
                        normalized: 'BBBBBB',
                        phone: 'BBBBBB',
                        streetaddress: 'BBBBBB',
                        postalcode: 'BBBBBB',
                        city: 'BBBBBB',
                        stateProvince: 'BBBBBB',
                        country: 'BBBBBB',
                        registered: currentDate.format(DATE_TIME_FORMAT),
                        lastactive: currentDate.format(DATE_TIME_FORMAT),
                        points: 1,
                        areaserviced: 'BBBBBB',
                        specialities: 'BBBBBB',
                        trades: 'BBBBBB',
                        monthYear: 'BBBBBB',
                        show: true,
                        hidden: 'BBBBBB',
                        licenceCycle: 1,
                        licenceNumber: 'BBBBBB'
                    },
                    elemDefault
                );
                const expected = Object.assign(
                    {
                        registered: currentDate,
                        lastactive: currentDate
                    },
                    returnedFromService
                );
                service
                    .query(expected)
                    .pipe(take(1), map(resp => resp.body))
                    .subscribe(body => expect(body).toContainEqual(expected));
                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(JSON.stringify([returnedFromService]));
                httpMock.verify();
            });

            it('should delete a Customer', async () => {
                const rxPromise = service.delete(123).subscribe(resp => expect(resp.ok));

                const req = httpMock.expectOne({ method: 'DELETE' });
                req.flush({ status: 200 });
            });
        });

        afterEach(() => {
            httpMock.verify();
        });
    });
});
