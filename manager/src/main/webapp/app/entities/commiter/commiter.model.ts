import { BaseEntity } from './../../shared';

export class Commiter implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public clazz?: string,
        public description?: string,
    ) {
    }
}
