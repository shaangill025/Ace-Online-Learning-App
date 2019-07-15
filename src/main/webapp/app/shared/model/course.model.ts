import { ITopic } from 'app/shared/model//topic.model';
import { ITags } from 'app/shared/model//tags.model';

export interface ICourse {
    id?: number;
    title?: string;
    section?: string;
    normCourses?: string;
    description?: string;
    amount?: number;
    imageContentType?: string;
    image?: any;
    point?: number;
    credit?: string;
    country?: string;
    state?: string;
    show?: boolean;
    topic?: ITopic;
    tags?: ITags[];
}

export class Course implements ICourse {
    constructor(
        public id?: number,
        public title?: string,
        public section?: string,
        public normCourses?: string,
        public description?: string,
        public amount?: number,
        public imageContentType?: string,
        public image?: any,
        public point?: number,
        public credit?: string,
        public country?: string,
        public state?: string,
        public show?: boolean,
        public topic?: ITopic,
        public tags?: ITags[]
    ) {
        this.show = this.show || false;
    }
}
