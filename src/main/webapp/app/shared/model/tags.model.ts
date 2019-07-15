export interface ITags {
    id?: number;
    name?: string;
    description?: string;
}

export class Tags implements ITags {
    constructor(public id?: number, public name?: string, public description?: string) {}
}
