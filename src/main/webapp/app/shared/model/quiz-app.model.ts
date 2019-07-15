import { IQuiz } from 'app/shared/model//quiz.model';
import { ISection } from 'app/shared/model//section.model';
import { IQuestion } from 'app/shared/model//question.model';

export interface IQuizApp {
    id?: number;
    quiz?: IQuiz;
    currSection?: ISection;
    newSection?: ISection;
}

export class QuizApp implements IQuizApp {
    constructor(
        public id?: number,
        public quiz?: IQuiz,
        public currSection?: ISection,
        public newSection?: ISection,
    ) {}
}
