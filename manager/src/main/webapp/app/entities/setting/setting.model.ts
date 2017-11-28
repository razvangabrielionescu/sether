import { BaseEntity } from './../../shared';

export class Setting implements BaseEntity {
    constructor(
        public id?: number,
        public stayOnDomain?: boolean,
        public stayOnPort?: boolean,
        public stayOnProtocol?: boolean,
        public numThreads?: number,
        public maxDepth?: number,
        public filterExtensions?: string,
        public project?: BaseEntity,
    ) {
        this.stayOnDomain = false;
        this.stayOnPort = false;
        this.stayOnProtocol = false;
    }
}
