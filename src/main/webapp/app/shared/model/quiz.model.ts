import { ISection } from 'app/shared/model//section.model';

export interface IQuiz {
    id?: number;
    name?: string;
    difficulty?: string;
    passingscore?: number;
    newSection?: ISection;
}

export class Quiz implements IQuiz {
    constructor(
        public id?: number,
        public name?: string,
        public difficulty?: string,
        public passingscore?: number,
        public newSection?: ISection
    ) {}
}
