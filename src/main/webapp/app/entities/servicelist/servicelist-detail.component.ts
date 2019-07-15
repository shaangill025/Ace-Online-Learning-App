import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IServicelist } from 'app/shared/model/servicelist.model';

@Component({
    selector: 'jhi-servicelist-detail',
    templateUrl: './servicelist-detail.component.html'
})
export class ServicelistDetailComponent implements OnInit {
    servicelist: IServicelist;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ servicelist }) => {
            this.servicelist = servicelist;
        });
    }

    previousState() {
        window.history.back();
    }
}
