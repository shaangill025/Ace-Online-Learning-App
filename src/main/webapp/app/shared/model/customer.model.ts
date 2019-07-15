import { Moment } from 'moment';
import { ICompany } from 'app/shared/model//company.model';
import { IUser } from 'app/core/user/user.model';

export const enum TYPES {
    RESIDENCE = 'RESIDENCE',
    COMMERCIAL = 'COMMERCIAL',
    INDUSTRIAL = 'INDUSTRIAL'
}

export interface ICustomer {
    id?: number;
    normalized?: string;
    phone?: string;
    streetaddress?: string;
    postalcode?: string;
    city?: string;
    stateProvince?: string;
    country?: string;
    registered?: Moment;
    lastactive?: Moment;
    points?: number;
    areaserviced?: string;
    specialities?: TYPES;
    trades?: string;
    monthYear?: string;
    show?: boolean;
    hidden?: string;
    licenceCycle?: number;
    licenceNumber?: string;
    company?: ICompany;
    user?: IUser;
}

export class Customer implements ICustomer {
    constructor(
        public id?: number,
        public normalized?: string,
        public phone?: string,
        public streetaddress?: string,
        public postalcode?: string,
        public city?: string,
        public stateProvince?: string,
        public country?: string,
        public registered?: Moment,
        public lastactive?: Moment,
        public points?: number,
        public areaserviced?: string,
        public specialities?: TYPES,
        public trades?: string,
        public monthYear?: string,
        public show?: boolean,
        public hidden?: string,
        public licenceCycle?: number,
        public licenceNumber?: string,
        public company?: ICompany,
        public user?: IUser
    ) {
        this.show = this.show || false;
    }
}
