import { Moment } from 'moment';
import { ICustomer } from 'app/shared/model//customer.model';
import { ICourse } from 'app/shared/model//course.model';

export interface ICourseHistory {
    id?: number;
    startdate?: Moment;
    lastactivedate?: Moment;
    isactive?: boolean;
    iscompleted?: boolean;
    access?: boolean;
    customer?: ICustomer;
    course?: ICourse;
}

export class CourseHistory implements ICourseHistory {
    constructor(
        public id?: number,
        public startdate?: Moment,
        public lastactivedate?: Moment,
        public isactive?: boolean,
        public iscompleted?: boolean,
        public access?: boolean,
        public customer?: ICustomer,
        public course?: ICourse
    ) {
        this.isactive = this.isactive || false;
        this.iscompleted = this.iscompleted || false;
        this.access = this.access || false;
    }
}
