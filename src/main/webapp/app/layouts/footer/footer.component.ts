import { Component, OnInit } from '@angular/core';
import { SERVER_API_URL } from 'app/app.constants';
import { HttpClient } from '@angular/common/http';

@Component({
    selector: 'jhi-footer',
    templateUrl: './footer.component.html',
    styleUrls: ['./footer-distributed-with-contact-form.css']
})
export class FooterComponent implements OnInit {
    email: string;
    message: string;
    contactname: string;
    notSent: boolean;
    constructor(private http: HttpClient) {
        this.notSent = false;
    }

    ngOnInit() {
        this.notSent = false;
    }

    processForm() {
        const resourceEmailUrl = SERVER_API_URL + 'api/email/contactform';
        this.http.get(`${resourceEmailUrl}/${this.contactname}?email=` + this.email + `&msg=` + this.message, {}).subscribe(flag => {
            this.notSent = !!flag;
        });
    }
}
