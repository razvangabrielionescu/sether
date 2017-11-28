import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SpongeSharedModule } from '../../shared';
import {
    SettingService,
    SettingPopupService,
    SettingComponent,
    SettingDetailComponent,
    SettingDialogComponent,
    SettingPopupComponent,
    SettingDeletePopupComponent,
    SettingDeleteDialogComponent,
    settingRoute,
    settingPopupRoute,
} from './';

const ENTITY_STATES = [
    ...settingRoute,
    ...settingPopupRoute,
];

@NgModule({
    imports: [
        SpongeSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        SettingComponent,
        SettingDetailComponent,
        SettingDialogComponent,
        SettingDeleteDialogComponent,
        SettingPopupComponent,
        SettingDeletePopupComponent,
    ],
    entryComponents: [
        SettingComponent,
        SettingDialogComponent,
        SettingPopupComponent,
        SettingDeleteDialogComponent,
        SettingDeletePopupComponent,
    ],
    providers: [
        SettingService,
        SettingPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SpongeSettingModule {}
