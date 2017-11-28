import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SpongeSharedModule } from '../../shared';
import {
    CommiterService,
    CommiterDetailComponent,
    commiterRoute,
} from './';

const ENTITY_STATES = [
    ...commiterRoute,
];

@NgModule({
    imports: [
        SpongeSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        CommiterDetailComponent,
    ],
    entryComponents: [
    ],
    providers: [
        CommiterService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SpongeCommiterModule {}
