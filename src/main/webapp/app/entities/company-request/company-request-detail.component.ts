import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICompanyRequest } from 'app/shared/model/company-request.model';

@Component({
    selector: 'jhi-company-request-detail',
    templateUrl: './company-request-detail.component.html'
})
export class CompanyRequestDetailComponent implements OnInit {
    companyRequest: ICompanyRequest;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ companyRequest }) => {
            this.companyRequest = companyRequest;
        });
    }

    previousState() {
        window.history.back();
    }
}
