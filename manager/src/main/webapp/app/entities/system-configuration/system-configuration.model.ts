import { BaseEntity } from './../../shared';

export class SystemConfiguration implements BaseEntity {
    constructor(
        public id?: number,
        public configKey?: string,
        public configValue?: string,
    ) {
    }
}
