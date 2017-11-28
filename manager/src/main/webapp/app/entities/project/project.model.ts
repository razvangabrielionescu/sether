import { BaseEntity } from './../../shared';
import {Spider} from '../spider/spider.model';
import {Status} from './status.model';
import {CommiterConfig} from '../commiter-config/commiter-config.model';
import {Agent} from '../agent/agent.model';
import {Job} from '../job/job.model';
import {Setting} from '../setting/setting.model';

export class Project implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public tool?: string,
        public spiders?: Spider[],
        public status?: Status,
        public show?: boolean,
        public commiterConfig?:  CommiterConfig,
        public agent?: Agent,
        public job?: Job,
        public schedulePeriod?: number,
        public scheduleUnit?: string,
        public setting?: Setting,
        public webUiProjectId?: string,
        public tableName?: string,
        public startUrl?: string
    ) {
    }
}
