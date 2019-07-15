import { EventEmitter, Injectable } from '@angular/core';
import { ICart } from 'app/shared/model/cart.model';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ISectionHistory } from 'app/shared/model/section-history.model';

@Injectable({
    providedIn: 'root'
})
export class NavbarService {
    public check = false;
    public checkout = false;
    public isCanada = false;
    public isUSA = false;
    public moveToOtherSite = false;
    public isAdBlocked = false;
    constructor(private http: HttpClient) {}

    public shouldCheck() {
        this.check = true;
    }

    public donotCheck() {
        this.check = false;
    }

    public isAdIsBlocked() {
        this.isAdBlocked = true;
    }

    public isAdIsNotBlocked() {
        this.isAdBlocked = false;
    }

    public getAdBlocked(): boolean {
        return this.isAdBlocked;
    }

    public getCheck(): boolean {
        return this.check;
    }

    public isCheckout() {
        this.checkout = true;
    }

    public noCheckout() {
        this.checkout = false;
    }

    public getCheckout(): boolean {
        return this.checkout;
    }

    public getifUSA() {
        return this.isUSA;
    }

    public setisUSA() {
        this.isUSA = true;
    }

    public getifCanada() {
        return this.isCanada;
    }

    public resetCountry() {
        this.isCanada = false;
        this.isUSA = false;
    }

    public setisCanada() {
        this.isCanada = true;
    }

    public getMovetoOtherSite() {
        return this.moveToOtherSite;
    }

    public setMovetoOtherSite(flag: boolean) {
        this.moveToOtherSite = flag;
    }

    public getcountryfromip() {
        this.resetCountry();
        let country = 'undefined';
        this.http.get('https://api.ipdata.co/?api-key=89cf2cffd686d2ee354ee8ce263641f4b2557abd8cf1fb1ecd9af94f').subscribe(responce => {
            console.log('Your Connection data: ' + responce);
            country = responce['country_code'];
            console.log('country is ' + country);
            if (country === 'CA') {
                this.setisCanada();
            } else if (country === 'US') {
                this.setisUSA();
            }
        });
    }
}
