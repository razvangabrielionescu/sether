import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SpongeSharedModule } from '../../shared';
import {
    CommiterConfigService,
    CommiterConfigPopupService,
    CommiterConfigComponent,
    CommiterConfigDetailComponent,
    CommiterConfigDialogComponent,
    CommiterConfigPopupComponent,
    CommiterConfigDeletePopupComponent,
    CommiterConfigDeleteDialogComponent,
    CommiterConfigClonePopupComponent,
    CommiterConfigCloneDialogComponent,
    commiterConfigRoute,
    commiterConfigPopupRoute,
} from './';

const ENTITY_STATES = [
    ...commiterConfigRoute,
    ...commiterConfigPopupRoute,
];

@NgModule({
    imports: [
        SpongeSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        CommiterConfigComponent,
        CommiterConfigDetailComponent,
        CommiterConfigDialogComponent,
        CommiterConfigDeleteDialogComponent,
        CommiterConfigCloneDialogComponent,
        CommiterConfigCloneDialogComponent,
        CommiterConfigPopupComponent,
        CommiterConfigDeletePopupComponent,
        CommiterConfigClonePopupComponent,
    ],
    entryComponents: [
        CommiterConfigComponent,
        CommiterConfigDialogComponent,
        CommiterConfigPopupComponent,
        CommiterConfigDeleteDialogComponent,
        CommiterConfigDeletePopupComponent,
        CommiterConfigCloneDialogComponent,
        CommiterConfigClonePopupComponent,
    ],
    providers: [
        CommiterConfigService,
        CommiterConfigPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SpongeCommiterConfigModule {}
