import { BaseEntity } from './../../shared';

export class Job implements BaseEntity {
    constructor(
        public id?: number,
        public startTime?: any,
        public endTime?: any,
        public period?: number,
        public periodUnit?: string,
        public status?: string,
        public project?: BaseEntity,
    ) {
    }
}
