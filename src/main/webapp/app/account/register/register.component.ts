import { Component, OnInit, AfterViewInit, Renderer, ElementRef, ViewChild, NgZone } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiLanguageService } from 'ng-jhipster';
import { ActivatedRoute } from '@angular/router';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { LoginService, Account, IUser, UserService, Principal } from 'app/core';
import { EMAIL_ALREADY_USED_TYPE, LOGIN_ALREADY_USED_TYPE } from 'app/shared';
import { LoginModalService } from 'app/core';
import { Register } from './register.service';
import { CustomerService } from 'app/entities/customer';
import { Customer, ICustomer } from 'app/shared/model/customer.model';
import { ICompany } from 'app/shared/model/company.model';
import { CompanyService } from 'app/entities/company';
import { MapsAPILoader } from '@agm/core';
import { GooglePlaceDirective } from 'ngx-google-places-autocomplete';
import { Address } from 'ngx-google-places-autocomplete/objects/address';
import { AddressComponent } from 'ngx-google-places-autocomplete/objects/addressComponent';
import { ComponentRestrictions } from 'ngx-google-places-autocomplete/objects/options/componentRestrictions';

// import moment = require('moment');

@Component({
    selector: 'jhi-register',
    templateUrl: './register.component.html'
})
export class RegisterComponent implements OnInit, AfterViewInit {
    confirmPassword: string;
    doNotMatch: string;
    error: string;
    errorEmailExists: string;
    errorUserExists: string;
    registerAccount: any;
    errorDuplicate = false;
    success: boolean;
    modalRef: NgbModalRef;
    companies: ICompany[];
    customer: ICustomer;
    public addr: string;
    public city: string;
    public country: string;
    public state: string;
    public date: string;
    public apt = '';
    public postCode: string;
    public yearDOB: string;
    public month: string;
    public license: string;
    hidden: string;
    @ViewChild('search') public searchElement: ElementRef;
    @ViewChild('places') places: GooglePlaceDirective;

    constructor(
        private languageService: JhiLanguageService,
        private loginModalService: LoginModalService,
        private loginService: LoginService,
        private principal: Principal,
        private customerService: CustomerService,
        private registerService: Register,
        private elementRef: ElementRef,
        private renderer: Renderer,
        private companyService: CompanyService,
        private activatedRoute: ActivatedRoute,
        private dataUtils: JhiDataUtils,
        private jhiAlertService: JhiAlertService,
        private mapsAPILoader: MapsAPILoader,
        private ngZone: NgZone,
        private http: HttpClient
    ) {}

    ngOnInit() {
        this.mapsAPILoader.load().then(() => {
            const autocomplete = new google.maps.places.Autocomplete(this.searchElement.nativeElement, { types: ['address'] });
            autocomplete.addListener('place_changed', () => {
                this.ngZone.run(() => {
                    const place: google.maps.places.PlaceResult = autocomplete.getPlace();
                    if (place.geometry === undefined || place.geometry === null) {
                        return;
                    }
                    for (let i = 0; i < place.address_components.length; i++) {
                        const addressType = place.address_components[i].types[0];
                        switch (addressType) {
                            case 'street_number':
                                this.addr = place.address_components[i].long_name;
                                break;
                            case 'route':
                                if (
                                    place.address_components[i].short_name !== undefined ||
                                    place.address_components[i].short_name !== null
                                ) {
                                    this.addr += ' ' + place.address_components[i].short_name;
                                }
                                break;
                            case 'locality':
                                this.city = place.address_components[i].long_name;
                                break;
                            case 'administrative_area_level_1':
                                this.state = place.address_components[i].long_name;
                                break;
                            case 'country':
                                this.country = place.address_components[i].short_name;
                                break;
                        }
                    }
                });
            });
        });
        this.loginService.logout();
        this.success = false;
        this.registerAccount = {};
        this.companyService.query().subscribe(
            (res: HttpResponse<ICompany[]>) => {
                this.companies = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        // this.date = this.registerAccount.company.cycledate;
    }

    public onChange(address: Address) {
        this.addr = this.getComponentByType(address, 'street_number').short_name;
        this.addr += ' ' + this.getComponentByType(address, 'route').long_name;
        this.addr += ', ' + this.getComponentByType(address, 'locality').long_name;
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
    public changeConfig() {
        this.places.options.componentRestrictions = new ComponentRestrictions({ country: 'us' });
        this.places.reset();
    }

    selectCompany(company: ICompany) {
        this.date = company.licenceCycle.toString();
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    extractFromAdress(components: any, type: string) {
        for (let i = 0; i < components.length; i++) {
            for (let j = 0; j < components[i].types.length; j++) {
                if (components[i].types[j] === type) {
                    return components[i].long_name;
                }
            }
            return '';
        }
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }

    setFileData(event, entity, field, isImage) {
        this.dataUtils.setFileData(event, entity, field, isImage);
    }

    previousState() {
        window.history.back();
    }

    ngAfterViewInit() {
        this.renderer.invokeElementMethod(this.elementRef.nativeElement.querySelector('#login'), 'focus', []);
    }

    register() {
        this.errorDuplicate = false;
        this.registerAccount.licenseNumber = this.license;
        if (this.apt !== '') {
            this.registerAccount.streetaddress = '#' + this.apt + this.addr;
        } else {
            this.registerAccount.streetaddress = this.addr;
        }
        this.registerAccount.city = this.city;
        this.registerAccount.country = this.country;
        this.registerAccount.stateProvince = this.state;
        this.registerAccount.monthYear = this.month + '/' + this.yearDOB;
        this.registerAccount.postalcode = this.postCode;
        // console.log('license Number' + this.registerAccount.licenseNumber);
        this.registerAccount.hidden =
            this.registerAccount.firstName +
            ' ' +
            this.registerAccount.lastName +
            '|' +
            this.registerAccount.email +
            '|' +
            this.license +
            '|' +
            this.registerAccount.phone +
            '|' +
            this.registerAccount.monthYear;
        if (this.registerAccount.password !== this.confirmPassword) {
            this.doNotMatch = 'ERROR';
        } else {
            this.doNotMatch = null;
            this.error = null;
            this.errorUserExists = null;
            this.errorEmailExists = null;
            this.languageService.getCurrent().then(key => {
                this.registerAccount.langKey = key;
                this.registerService.save(this.registerAccount).subscribe(
                    () => {
                        this.success = true;
                    },
                    response => this.processError(response)
                );
            });
        }
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    private onSaveSuccess() {
        this.success = false;
        this.previousState();
    }

    private onSaveError() {
        this.success = false;
    }

    trackCompanyById(index: number, item: ICompany) {
        return item.id;
    }

    openLogin() {
        this.modalRef = this.loginModalService.open();
    }

    private processError(response: HttpErrorResponse) {
        this.success = null;
        if (response.status === 400 && response.error.type === LOGIN_ALREADY_USED_TYPE) {
            this.errorUserExists = 'ERROR';
        } else if (response.status === 400 && response.error.type === EMAIL_ALREADY_USED_TYPE) {
            this.errorEmailExists = 'ERROR';
        } else if (response.status === 500) {
            this.errorDuplicate = true;
        } else {
            this.error = 'ERROR';
        }
    }
}
