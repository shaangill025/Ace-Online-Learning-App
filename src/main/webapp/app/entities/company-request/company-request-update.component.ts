import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GooglePlaceDirective } from 'ngx-google-places-autocomplete';
import { ICompanyRequest } from 'app/shared/model/company-request.model';
import { CompanyRequestService } from './company-request.service';
import { Address } from 'ngx-google-places-autocomplete/objects/address';
import { AddressComponent } from 'ngx-google-places-autocomplete/objects/addressComponent';
@Component({
    selector: 'jhi-company-request-update',
    templateUrl: './company-request-update.component.html'
})
export class CompanyRequestUpdateComponent implements OnInit {
    companyRequest: ICompanyRequest;
    isSaving: boolean;
    public addr: string;
    public city: string;
    public country: string;
    public state: string;
    public postCode: string;
    @ViewChild('places') places: GooglePlaceDirective;
    constructor(protected companyRequestService: CompanyRequestService, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ companyRequest }) => {
            this.companyRequest = companyRequest;
            this.addr = this.companyRequest.streetAddress;
            this.city = this.companyRequest.city;
            this.country = this.companyRequest.country;
            this.state = this.companyRequest.stateProvince;
            this.postCode = this.companyRequest.postalCode;
        });
    }

    previousState() {
        window.history.back();
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

    save() {
        this.isSaving = true;
        this.companyRequest.streetAddress = this.addr;
        this.companyRequest.city = this.city;
        this.companyRequest.country = this.country;
        this.companyRequest.stateProvince = this.state;
        this.companyRequest.postalCode = this.postCode;
        if (this.companyRequest.id !== undefined) {
            this.subscribeToSaveResponse(this.companyRequestService.update(this.companyRequest));
        } else {
            this.subscribeToSaveResponse(this.companyRequestService.create(this.companyRequest));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<ICompanyRequest>>) {
        result.subscribe((res: HttpResponse<ICompanyRequest>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }
}
