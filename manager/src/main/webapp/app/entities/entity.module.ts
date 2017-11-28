import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { SpongeProjectModule } from './project/project.module';
import { SpongeSpiderModule } from './spider/spider.module';
import { SpongeSystemConfigurationModule } from './system-configuration/system-configuration.module';
import { SpongeJobModule } from './job/job.module';
import { SpongeCommiterModule } from './commiter/commiter.module';
import { SpongeCommiterConfigModule } from './commiter-config/commiter-config.module';
import { SpongeCommiterFieldMappingModule } from './commiter-field-mapping/commiter-field-mapping.module';
import { SpongeAgentModule } from './agent/agent.module';
import {SpongeSettingModule} from './setting/setting.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        SpongeProjectModule,
        SpongeSpiderModule,
        SpongeSystemConfigurationModule,
        SpongeJobModule,
        SpongeCommiterModule,
        SpongeCommiterConfigModule,
        SpongeCommiterFieldMappingModule,
        SpongeAgentModule,
        SpongeSettingModule
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SpongeEntityModule {}
