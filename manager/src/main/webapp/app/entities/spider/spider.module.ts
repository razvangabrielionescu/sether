import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SpongeSharedModule } from '../../shared';
import {
    SpiderService,
    SpiderPopupService,
    SpiderComponent,
    SpiderDetailComponent,
    SpiderDialogComponent,
    SpiderPopupComponent,
    SpiderDeletePopupComponent,
    SpiderDeleteDialogComponent,
    spiderRoute,
    spiderPopupRoute,
} from './';

const ENTITY_STATES = [
    ...spiderRoute,
    ...spiderPopupRoute,
];

@NgModule({
    imports: [
        SpongeSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        SpiderComponent,
        SpiderDetailComponent,
        SpiderDialogComponent,
        SpiderDeleteDialogComponent,
        SpiderPopupComponent,
        SpiderDeletePopupComponent,
    ],
    entryComponents: [
        SpiderComponent,
        SpiderDialogComponent,
        SpiderPopupComponent,
        SpiderDeleteDialogComponent,
        SpiderDeletePopupComponent,
    ],
    providers: [
        SpiderService,
        SpiderPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SpongeSpiderModule {}
