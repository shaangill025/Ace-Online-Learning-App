import { ISection } from 'app/shared/model//section.model';

export interface IBookmark {
    id?: number;
    text?: string;
    slide?: number;
    timestamp?: string;
    module?: string;
    seconds?: number;
    section?: ISection;
}

export class Bookmark implements IBookmark {
    constructor(
        public id?: number,
        public text?: string,
        public slide?: number,
        public timestamp?: string,
        public module?: string,
        public seconds?: number,
        public section?: ISection
    ) {}
}
