import { ICustomer } from 'app/shared/model//customer.model';
import { ICourse } from 'app/shared/model//course.model';

export interface ILegacyCourses {
    id?: number;
    title?: string;
    description?: string;
    amount?: number;
    imageContentType?: string;
    image?: any;
    province?: string;
    customer?: ICustomer;
    course?: ICourse;
}

export class LegacyCourses implements ILegacyCourses {
    constructor(
        public id?: number,
        public title?: string,
        public description?: string,
        public amount?: number,
        public imageContentType?: string,
        public image?: any,
        public province?: string,
        public customer?: ICustomer,
        public course?: ICourse
    ) {}
}
