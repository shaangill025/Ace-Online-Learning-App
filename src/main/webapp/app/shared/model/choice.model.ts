import { IQuestion } from 'app/shared/model//question.model';

export interface IChoice {
    id?: number;
    textChoice?: string;
    isanswer?: boolean;
    question?: IQuestion;
}

export class Choice implements IChoice {
    constructor(public id?: number, public textChoice?: string, public isanswer?: boolean, public question?: IQuestion) {
        this.isanswer = this.isanswer || false;
    }
}
