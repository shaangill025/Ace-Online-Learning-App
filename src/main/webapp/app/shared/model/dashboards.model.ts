export interface IDashboards {
    id?: number;
}

export class Dashboards implements IDashboards {
    constructor(public id?: number) {}
}
