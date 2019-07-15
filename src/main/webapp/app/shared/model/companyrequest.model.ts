export interface ICompanyrequest {
    id?: number;
    name?: string;
    description?: string;
    phone?: string;
    email?: string;
    streetAddress?: string;
    postalCode?: string;
    city?: string;
    stateProvince?: string;
    country?: string;
    url?: string;
    licenceCycle?: string;
}

export class Companyrequest implements ICompanyrequest {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public phone?: string,
        public email?: string,
        public streetAddress?: string,
        public postalCode?: string,
        public city?: string,
        public stateProvince?: string,
        public country?: string,
        public url?: string,
        public licenceCycle?: string
    ) {}
}
