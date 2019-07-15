import { IQuiz } from 'app/shared/model//quiz.model';
import { ITags } from 'app/shared/model//tags.model';
import { ICourse } from 'app/shared/model//course.model';

export interface ISection {
    id?: number;
    name?: string;
    notes?: string;
    normSection?: string;
    contentContentType?: string;
    content?: any;
    videoUrl?: string;
    textcontent?: any;
    type?: string;
    pdfUrl?: string;
    totalPages?: number;
    quiz?: IQuiz;
    tags?: ITags[];
    course?: ICourse;
}

export class Section implements ISection {
    constructor(
        public id?: number,
        public name?: string,
        public notes?: string,
        public normSection?: string,
        public contentContentType?: string,
        public content?: any,
        public videoUrl?: string,
        public textcontent?: any,
        public type?: string,
        public pdfUrl?: string,
        public totalPages?: number,
        public quiz?: IQuiz,
        public tags?: ITags[],
        public course?: ICourse
    ) {}
}
