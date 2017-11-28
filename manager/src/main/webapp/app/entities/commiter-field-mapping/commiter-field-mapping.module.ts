import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SpongeSharedModule } from '../../shared';
import {
    CommiterFieldMappingService,
    CommiterFieldMappingPopupService,
    CommiterFieldMappingComponent,
    CommiterFieldMappingDetailComponent,
    CommiterFieldMappingDialogComponent,
    CommiterFieldMappingPopupComponent,
    CommiterFieldMappingDeletePopupComponent,
    CommiterFieldMappingDeleteDialogComponent,
    commiterFieldMappingRoute,
    commiterFieldMappingPopupRoute,
} from './';

const ENTITY_STATES = [
    ...commiterFieldMappingRoute,
    ...commiterFieldMappingPopupRoute,
];

@NgModule({
    imports: [
        SpongeSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        CommiterFieldMappingComponent,
        CommiterFieldMappingDetailComponent,
        CommiterFieldMappingDialogComponent,
        CommiterFieldMappingDeleteDialogComponent,
        CommiterFieldMappingPopupComponent,
        CommiterFieldMappingDeletePopupComponent,
    ],
    entryComponents: [
        CommiterFieldMappingComponent,
        CommiterFieldMappingDialogComponent,
        CommiterFieldMappingPopupComponent,
        CommiterFieldMappingDeleteDialogComponent,
        CommiterFieldMappingDeletePopupComponent,
    ],
    providers: [
        CommiterFieldMappingService,
        CommiterFieldMappingPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SpongeCommiterFieldMappingModule {}
