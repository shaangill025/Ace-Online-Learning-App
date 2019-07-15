import { AfterViewChecked, Component, NgZone, OnInit, ViewChild } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { LoginModalService, Principal, Account, UserService } from 'app/core';
import { CustomerService } from 'app/entities/customer';
import { ICustomer } from 'app/shared/model/customer.model';
import * as setInterval from 'core-js/library/fn/set-interval';
import { NavbarService } from 'app/layouts/navbar/navbar.service';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ElementRef } from '@angular/core';
import adBlocker from 'just-detect-adblock';
import { addSubtract } from 'ngx-bootstrap/chronos/moment/add-subtract';
@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit {
    account: Account;
    modalRef: NgbModalRef;
    custEmail: string;
    customer: ICustomer;
    name: string;
    init = false;
    checkRole: boolean;
    isCanada = false;
    isUSA = false;
    errorRegionLock = false;
    shouldnotbeinUsa = false;
    shouldnotbeinCanada = false;
    regionOutside = false;
    errorAdBlock = false;
    @ViewChild('adsBanner') ads: ElementRef;

    constructor(
        private principal: Principal,
        private loginModalService: LoginModalService,
        private eventManager: JhiEventManager,
        private userService: UserService,
        private customerService: CustomerService,
        private zone: NgZone,
        private navbarService: NavbarService,
        private router: Router,
        private http: HttpClient
    ) {}

    ngOnInit() {
        this.principal.identity().then(account => {
            this.account = account;
        });
        if (this.router.url.includes('/', 0)) {
            setInterval(() => {
                this.checkAdBlock();
            }, 5000);
        }
        setInterval(() => {
            this.checkRole = this.principal.hasAnyAuthorityDirect(['ROLE_USER', 'ROLE_ADMIN']);
        }, 500);
        setTimeout(() => {
            this.isCanada = this.navbarService.getifCanada();
            this.isUSA = this.navbarService.getifUSA();
            // console.log('Home - is Canada' + this.isCanada);
            // console.log('Home - is USA' + this.isUSA);
            this.checkRegionUrl();
        }, 1000);
        this.registerAuthenticationSuccess();
    }

    checkAdBlock() {
        if (this.navbarService.getAdBlocked()) {
            this.errorAdBlock = true;
        } else {
            this.errorAdBlock = false;
        }
    }

    checkRegionUrl() {
        if (this.isCanada && location.hostname.includes('aceaol.com')) {
            this.errorRegionLock = true;
            this.shouldnotbeinUsa = true;
        } else if (this.isUSA && location.hostname.includes('aceaol.ca')) {
            this.errorRegionLock = true;
            this.shouldnotbeinCanada = true;
        } else if (!this.isCanada && !this.isUSA) {
            this.errorRegionLock = true;
            this.regionOutside = true;
        }
        console.log('Inside region lock function, is Canada' + this.isCanada);
        console.log('Inside region lock function, is USA' + this.isUSA);
        console.log('Inside region lock function, ErrorRegionLock' + this.errorRegionLock);
        console.log('Inside region lock function, ShouldNotBeinCanada' + this.shouldnotbeinCanada);
        console.log('Inside region lock function, ShouldNotbeinUSA' + this.shouldnotbeinUsa);
    }

    /** ngAfterViewChecked() {
        location.reload();
    }*/

    public reloadPage() {
        location.reload();
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', message => {
            this.principal.identity().then(account => {
                this.account = account;
            });
        });
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }
}
