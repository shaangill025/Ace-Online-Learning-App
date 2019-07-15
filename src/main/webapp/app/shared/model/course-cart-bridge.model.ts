import { Moment } from 'moment';
import { ICart } from 'app/shared/model//cart.model';
import { ICourse } from 'app/shared/model//course.model';

export interface ICourseCartBridge {
    id?: number;
    timestamp?: Moment;
    cart?: ICart;
    course?: ICourse;
}

export class CourseCartBridge implements ICourseCartBridge {
    constructor(public id?: number, public timestamp?: Moment, public cart?: ICart, public course?: ICourse) {}
}
