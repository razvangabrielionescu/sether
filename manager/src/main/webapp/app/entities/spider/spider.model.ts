import { BaseEntity } from './../../shared';
import {Status} from '../project/status.model';

export class Spider implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public status?: Status
    ) {
    }
}
