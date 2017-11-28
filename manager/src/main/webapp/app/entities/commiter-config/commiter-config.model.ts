import { BaseEntity } from './../../shared';

export class CommiterConfig implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public dbDriverClass?: string,
        public dbConnectionUrl?: string,
        public dbDriverPath?: string,
        public dbUsername?: string,
        public dbPassword?: string,
        public dbCreateMissing?: string,
        public dbCreateTableSQL?: string,
        public dbCommitBatchSize?: string,
        public bcUrl?: string,
        public bcUsername?: string,
        public bcPassword?: string,
        public fsDirectory?: string,
        public commiter?: BaseEntity,
        public commiterFieldMappings?: BaseEntity[],
        public projects?: BaseEntity[],
    ) {
    }
}
