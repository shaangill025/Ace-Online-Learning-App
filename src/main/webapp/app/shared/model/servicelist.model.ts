import { ICustomer } from 'app/shared/model//customer.model';

export interface IServicelist {
    id?: number;
    name?: string;
    company?: string;
    url?: string;
    phone?: string;
    email?: string;
    areas?: string;
    speciality?: string;
    trades?: string;
    customer?: ICustomer;
}

export class Servicelist implements IServicelist {
    constructor(
        public id?: number,
        public name?: string,
        public company?: string,
        public url?: string,
        public phone?: string,
        public email?: string,
        public areas?: string,
        public speciality?: string,
        public trades?: string,
        public customer?: ICustomer
    ) {}
}
