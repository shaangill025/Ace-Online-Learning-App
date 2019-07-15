import { IChoice } from 'app/shared/model//choice.model';
import { IQuiz } from 'app/shared/model//quiz.model';

export interface IQuestion {
    id?: number;
    textQuestion?: string;
    difficulty?: string;
    restudy?: string;
    used?: boolean;
    choices?: IChoice[];
    quiz?: IQuiz;
}

export class Question implements IQuestion {
    constructor(
        public id?: number,
        public textQuestion?: string,
        public difficulty?: string,
        public restudy?: string,
        public used?: boolean,
        public choices?: IChoice[],
        public quiz?: IQuiz
    ) {
        this.used = this.used || false;
    }
}
