import { AfterViewInit, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';
import * as jspdf from 'jspdf';
import * as html2canvas from 'html2canvas';
import { ICertificate } from 'app/shared/model/certificate.model';
import { CertificateService } from 'app/entities/certificate/certificate.service';
import { Account, Principal, IUser, UserService } from 'app/core';
import { Customer, ICustomer } from 'app/shared/model/customer.model';
import { Observable } from 'rxjs';
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import * as sgMail from '@sendgrid/mail';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared';
import { ICourse } from 'app/shared/model/course.model';
import { TimeCourseLogService } from 'app/entities/time-course-log';
import { CustomerService } from 'app/entities/customer';
import { courseHistoryPopupRoute, CourseHistoryService } from 'app/entities/course-history';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
    selector: 'jhi-certificate-detail',
    templateUrl: './certificate-detail.component.html'
})
export class CertificateDetailComponent implements OnInit, AfterViewInit {
    certificate: ICertificate;
    customer: ICustomer;
    user: IUser;
    pdf: jspdf;
    account: Account;
    userEmail: String;
    blob: Blob;
    arrayBuffer: ArrayBuffer;
    test: string;
    bufferLength: number;
    isSaving = false;
    bytes: any;
    timestamp: string;
    course: ICourse;
    timespent: number;
    fomrattedTime: string;
    accessError = false;
    constructor(
        private dataUtils: JhiDataUtils,
        private activatedRoute: ActivatedRoute,
        private certificateService: CertificateService,
        private principal: Principal,
        private timecourselogService: TimeCourseLogService,
        private userService: UserService,
        private customerService: CustomerService,
        private couseHistoryService: CourseHistoryService,
        private spinner: NgxSpinnerService
    ) {
        this.timespent = 0;
        this.fomrattedTime = '00:00';
    }

    ngAfterViewInit(): void {
        this.spinner.show();
        setTimeout(() => {
            /** spinner ends after 5 seconds */
            this.spinner.hide();
        }, 5000);
    }

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ certificate }) => {
            this.certificate = certificate;
        });
        this.principal.identity().then(account => {
            this.account = account;
            this.userEmail = this.account.email;
            this.userService.getlogin(account.login).subscribe(userId => {
                this.customerService.getuser(userId).subscribe(cust => {
                    if (cust.id === this.certificate.customer.id) {
                        this.accessError = false;
                    } else {
                        this.accessError = true;
                    }
                });
            });
        });
        setTimeout(() => {
            this.convertToPDF();
        }, 8000);
        /**this.certificate.pdfContentType = 'data:application/pdf;base64';
         this.certificateService.update(this.certificate);*/
        this.customer = this.certificate.customer;
        this.course = this.certificate.courseHistory.course;
        this.couseHistoryService.getbycustomerandcourse(this.course.id, this.customer.id).subscribe(courseHist => {
            this.timecourselogService.getspenttime(courseHist.id, this.customer.id).subscribe(log => {
                this.timespent = log.body;
                this.fomrattedTime = this.parseTime(this.timespent);
            });
        });
        // this.user = this.certificate.customer.user;
    }

    parseTime(totalSeconds: number) {
        let hrs: string | number = Math.floor(totalSeconds / 3600);
        if (Number(hrs) < 1) {
            let mins: string | number = Math.floor(totalSeconds / 60);
            let secs: string | number = Math.round(totalSeconds % 60);
            mins = (mins < 10 ? '0' : '') + mins;
            secs = (secs < 10 ? '0' : '') + secs;
            hrs = (hrs < 10 ? '0' : '') + hrs;
            return mins + ':' + secs;
        } else {
            let mins: string | number = Math.floor((totalSeconds % 3600) / 60);
            let secs: string | number = Math.round(totalSeconds % 60);
            mins = (mins < 10 ? '0' : '') + mins;
            secs = (secs < 10 ? '0' : '') + secs;
            hrs = (hrs < 10 ? '0' : '') + hrs;
            return hrs + ':' + mins + ':' + secs;
        }
    }

    convertToPDF() {
        const data = document.getElementById('convertPdf');
        html2canvas(data).then(canvas => {
            // Few necessary setting options
            const imgWidth = 300;
            const pageHeight = 400;
            const imgHeight = canvas.height * imgWidth / canvas.width;
            const heightLeft = imgHeight;
            const reader = new FileReader();
            const contentDataURL = canvas.toDataURL('image/png');
            this.pdf = new jspdf('l', 'mm', 'a4', 1);
            this.pdf.addImage(contentDataURL, 'PNG', 0, 0, imgWidth, imgHeight, '', 'FAST');
            this.pdf.save('certificate.pdf');
            this.blob = this.pdf.output('blob');
            this.arrayBuffer = this.pdf.output('arraybuffer');
            /**this.email();*/
            /**this.certificateService.email(this.certificate.id);*/
        });
    }

    email() {
        this.certificateService.email(this.certificate.id);
        /**this.certificateService.attachment(this.bufferToBase64(new Uint8Array(this.arrayBuffer)), this.certificate.id);*/
    }

    bufferToBase64(buffer: Uint8Array) {
        const binstr = Array.prototype.map
            .call(buffer, function(ch) {
                return String.fromCharCode(ch);
            })
            .join('');
        return btoa(binstr);
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    setFileData(event, entity, field, isImage) {
        this.dataUtils.setFileData(event, entity, field, isImage);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }

    previousState() {
        window.history.back();
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ICertificate>>) {
        result.subscribe((res: HttpResponse<ICertificate>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
    }

    private onSaveError() {
        this.isSaving = false;
    }
}
