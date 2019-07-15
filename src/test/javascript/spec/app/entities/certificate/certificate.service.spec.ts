/* tslint:disable max-line-length */
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { take, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { ICertificate, Certificate } from 'app/shared/model/certificate.model';

describe('Service Tests', () => {
    describe('Certificate Service', () => {
        let injector: TestBed;
        let service: CertificateService;
        let httpMock: HttpTestingController;
        let elemDefault: ICertificate;
        let currentDate: moment.Moment;
        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule]
            });
            injector = getTestBed();
            service = injector.get(CertificateService);
            httpMock = injector.get(HttpTestingController);
            currentDate = moment();

            elemDefault = new Certificate(0, currentDate, false);
        });

        describe('Service methods', async () => {
            it('should find an element', async () => {
                const returnedFromService = Object.assign(
                    {
                        timestamp: currentDate.format(DATE_TIME_FORMAT)
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

            it('should create a Certificate', async () => {
                const returnedFromService = Object.assign(
                    {
                        id: 0,
                        timestamp: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );
                const expected = Object.assign(
                    {
                        timestamp: currentDate
                    },
                    returnedFromService
                );
                service
                    .create(new Certificate(null))
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: expected }));
                const req = httpMock.expectOne({ method: 'POST' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should update a Certificate', async () => {
                const returnedFromService = Object.assign(
                    {
                        timestamp: currentDate.format(DATE_TIME_FORMAT),
                        isEmailed: true
                    },
                    elemDefault
                );

                const expected = Object.assign(
                    {
                        timestamp: currentDate
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

            it('should return a list of Certificate', async () => {
                const returnedFromService = Object.assign(
                    {
                        timestamp: currentDate.format(DATE_TIME_FORMAT),
                        isEmailed: true
                    },
                    elemDefault
                );
                const expected = Object.assign(
                    {
                        timestamp: currentDate
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

            it('should delete a Certificate', async () => {
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
