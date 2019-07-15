import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ICompany } from 'app/shared/model/company.model';
import { CompanyService } from './company.service';
import { Address } from 'ngx-google-places-autocomplete/objects/address';
import { AddressComponent } from 'ngx-google-places-autocomplete/objects/addressComponent';
import { GooglePlaceDirective } from 'ngx-google-places-autocomplete';

@Component({
    selector: 'jhi-company-update',
    templateUrl: './company-update.component.html'
})
export class CompanyUpdateComponent implements OnInit {
    company: ICompany;
    isSaving: boolean;
    public addr: string;
    public city: string;
    public country: string;
    public state: string;
    public postCode: string;
    @ViewChild('places') places: GooglePlaceDirective;
    constructor(protected companyService: CompanyService, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ company }) => {
            this.company = company;
            this.addr = this.company.streetAddress;
            this.city = this.company.city;
            this.country = this.company.country;
            this.state = this.company.stateProvince;
            this.postCode = this.company.postalCode;
        });
    }

    public onChange(address: Address) {
        this.addr = this.getComponentByType(address, 'street_number').long_name;
        this.addr += ' ' + this.getComponentByType(address, 'route').long_name;
        this.city = this.getComponentByType(address, 'locality').long_name;
        this.state = this.getComponentByType(address, 'administrative_area_level_1').long_name;
        this.country = this.getComponentByType(address, 'country').long_name;
        this.postCode = this.getComponentByType(address, 'postal_code').long_name;
    }

    public getComponentByType(address: Address, type: string): AddressComponent {
        if (!type) {
            return null;
        }
        if (!address || !address.address_components || address.address_components.length === 0) {
            return null;
        }
        type = type.toLowerCase();
        for (const comp of address.address_components) {
            if (!comp.types || comp.types.length === 0) {
                continue;
            }
            if (comp.types.findIndex(x => x.toLowerCase() === type) > -1) {
                return comp;
            }
        }
        return null;
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.company.streetAddress = this.addr;
        this.company.city = this.city;
        this.company.country = this.country;
        this.company.stateProvince = this.state;
        this.company.postalCode = this.postCode;
        if (this.company.id !== undefined) {
            this.subscribeToSaveResponse(this.companyService.update(this.company));
        } else {
            this.subscribeToSaveResponse(this.companyService.create(this.company));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<ICompany>>) {
        result.subscribe((res: HttpResponse<ICompany>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }
}
