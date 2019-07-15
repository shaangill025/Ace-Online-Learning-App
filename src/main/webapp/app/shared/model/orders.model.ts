import { Moment } from 'moment';
import { ICart } from 'app/shared/model//cart.model';

export const enum NOTIFICATIONS {
    ORDERPROCESSING = 'ORDERPROCESSING',
    COMPLETE = 'COMPLETE',
    CANCELLLED = 'CANCELLLED',
    REFUND = 'REFUND',
    ONHOLD = 'ONHOLD'
}

export const enum PAYMENT {
    PAYPAL = 'PAYPAL',
    STRIPE = 'STRIPE'
}

export interface IOrders {
    id?: number;
    createddate?: Moment;
    amount?: number;
    status?: NOTIFICATIONS;
    payment?: PAYMENT;
    gateway_id?: string;
    seller_message?: string;
    network_status?: string;
    seller_status?: string;
    gateway_amt?: string;
    seller_type?: string;
    card_type?: string;
    last4?: string;
    cart?: ICart;
}

export class Orders implements IOrders {
    constructor(
        public id?: number,
        public createddate?: Moment,
        public amount?: number,
        public status?: NOTIFICATIONS,
        public payment?: PAYMENT,
        public gateway_id?: string,
        public seller_message?: string,
        public network_status?: string,
        public seller_status?: string,
        public gateway_amt?: string,
        public seller_type?: string,
        public card_type?: string,
        public last4?: string,
        public cart?: ICart
    ) {}
}
