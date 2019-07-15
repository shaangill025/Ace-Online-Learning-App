import { Moment } from 'moment';
import { ICustomer } from 'app/shared/model//customer.model';
import { IQuiz } from 'app/shared/model//quiz.model';

export interface IQuizHistory {
    id?: number;
    start?: Moment;
    passed?: boolean;
    customer?: ICustomer;
    quiz?: IQuiz;
}

export class QuizHistory implements IQuizHistory {
    constructor(public id?: number, public start?: Moment, public passed?: boolean, public customer?: ICustomer, public quiz?: IQuiz) {
        this.passed = this.passed || false;
    }
}
