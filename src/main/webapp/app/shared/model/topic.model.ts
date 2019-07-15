export interface ITopic {
    id?: number;
    name?: string;
    description?: string;
    imageContentType?: string;
    image?: any;
}

export class Topic implements ITopic {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public imageContentType?: string,
        public image?: any
    ) {}
}
