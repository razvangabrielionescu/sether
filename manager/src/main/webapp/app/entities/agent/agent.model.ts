import { BaseEntity } from './../../shared';

export class Agent implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public host?: string,
        public port?: number,
        public projects?: BaseEntity[],
    ) {
    }
}
