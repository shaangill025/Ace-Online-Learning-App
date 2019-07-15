export interface ICheckout {
    id?: number;
}

export class Checkout implements ICheckout {
    constructor(public id?: number) {}
}
