import { Moment } from 'moment';
import { ICustomer } from 'app/shared/model//customer.model';
import { ICourseHistory } from 'app/shared/model//course-history.model';

export interface ICertificate {
    id?: number;
    timestamp?: Moment;
    isEmailed?: boolean;
    customer?: ICustomer;
    courseHistory?: ICourseHistory;
}

export class Certificate implements ICertificate {
    constructor(
        public id?: number,
        public timestamp?: Moment,
        public isEmailed?: boolean,
        public customer?: ICustomer,
        public courseHistory?: ICourseHistory
    ) {
        this.isEmailed = this.isEmailed || false;
    }
}
