export interface ITos {
    id?: number;
}

export class Tos implements ITos {
    constructor(public id?: number) {}
}
