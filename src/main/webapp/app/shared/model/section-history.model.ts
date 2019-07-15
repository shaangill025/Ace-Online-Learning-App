import { Moment } from 'moment';
import { ICustomer } from 'app/shared/model//customer.model';
import { ISection } from 'app/shared/model//section.model';

export interface ISectionHistory {
    id?: number;
    startdate?: Moment;
    lastactivedate?: Moment;
    watched?: boolean;
    stamp?: number;
    customer?: ICustomer;
    section?: ISection;
}

export class SectionHistory implements ISectionHistory {
    constructor(
        public id?: number,
        public startdate?: Moment,
        public lastactivedate?: Moment,
        public watched?: boolean,
        public stamp?: number,
        public customer?: ICustomer,
        public section?: ISection
    ) {
        this.watched = this.watched || false;
    }
}
