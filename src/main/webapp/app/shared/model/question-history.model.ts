import { Moment } from 'moment';
import { ICustomer } from 'app/shared/model//customer.model';
import { IQuestion } from 'app/shared/model//question.model';

export interface IQuestionHistory {
    id?: number;
    timestamp?: Moment;
    correct?: boolean;
    customer?: ICustomer;
    question?: IQuestion;
}

export class QuestionHistory implements IQuestionHistory {
    constructor(
        public id?: number,
        public timestamp?: Moment,
        public correct?: boolean,
        public customer?: ICustomer,
        public question?: IQuestion
    ) {
        this.correct = this.correct || false;
    }
}
