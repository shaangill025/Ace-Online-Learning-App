import { Moment } from 'moment';
import { ICustomer } from 'app/shared/model//customer.model';

export interface IMergeFunction {
    id?: number;
    datetime?: Moment;
    note?: string;
    tobeRemoved?: ICustomer;
    replacement?: ICustomer;
}

export class MergeFunction implements IMergeFunction {
    constructor(
        public id?: number,
        public datetime?: Moment,
        public note?: string,
        public tobeRemoved?: ICustomer,
        public replacement?: ICustomer
    ) {}
}
