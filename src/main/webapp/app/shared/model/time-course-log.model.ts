import { Moment } from 'moment';
import { ICustomer } from 'app/shared/model//customer.model';
import { ICourseHistory } from 'app/shared/model//course-history.model';

export interface ITimeCourseLog {
    id?: number;
    timespent?: number;
    recorddate?: Moment;
    customer?: ICustomer;
    courseHistory?: ICourseHistory;
}

export class TimeCourseLog implements ITimeCourseLog {
    constructor(
        public id?: number,
        public timespent?: number,
        public recorddate?: Moment,
        public customer?: ICustomer,
        public courseHistory?: ICourseHistory
    ) {}
}
