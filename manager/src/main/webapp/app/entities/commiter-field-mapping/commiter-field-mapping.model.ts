import { BaseEntity } from './../../shared';
import {CommiterConfig} from '../commiter-config/commiter-config.model';

export class CommiterFieldMapping implements BaseEntity {
    constructor(
        public id?: number,
        public sourceField?: string,
        public destinationField?: string,
        public commiterConfig?: CommiterConfig
    ) {
    }
}
