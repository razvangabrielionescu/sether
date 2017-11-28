import { BaseEntity } from './../../shared';
import {Spider} from '../spider/spider.model';

export class Status implements BaseEntity {
    constructor(
        public status?: string,
        public note?: string,
        public progress?: string,
        public lastActivity?: string
    ) {
    }
}
