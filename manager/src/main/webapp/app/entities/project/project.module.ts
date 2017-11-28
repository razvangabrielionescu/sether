import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SpongeSharedModule } from '../../shared';
import {
    ProjectService,
    ProjectPopupService,
    ProjectComponent,
    ProjectDetailComponent,
    ProjectDialogComponent,
    ProjectPopupComponent,
    ProjectDeletePopupComponent,
    ProjectDeleteDialogComponent,
    ProjectCommiterPopupComponent,
    ProjectCommiterDialogComponent,
    ProjectAgentPopupComponent,
    ProjectAgentDialogComponent,
    FileSystemPopupComponent,
    FileSystemDialogComponent,
    ProjectRunPopupComponent,
    ProjectRunDialogComponent,
    projectRoute,
    projectPopupRoute,
} from './';
import {SettingPopupService} from '../setting/setting-popup.service';
import {SettingService} from '../setting/setting.service';
import {PagerService} from './sqlviewer/pager.service';
import {SqlViewerComponent} from './sqlviewer/sqlviewer.component';

const ENTITY_STATES = [
    ...projectRoute,
    ...projectPopupRoute,
];

@NgModule({
    imports: [
        SpongeSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        ProjectComponent,
        ProjectDetailComponent,
        ProjectDialogComponent,
        ProjectDeleteDialogComponent,
        ProjectPopupComponent,
        ProjectDeletePopupComponent,
        ProjectCommiterPopupComponent,
        ProjectCommiterDialogComponent,
        ProjectAgentPopupComponent,
        ProjectAgentDialogComponent,
        FileSystemPopupComponent,
        FileSystemDialogComponent,
        ProjectRunPopupComponent,
        ProjectRunDialogComponent,
        SqlViewerComponent
    ],
    entryComponents: [
        ProjectComponent,
        ProjectDialogComponent,
        ProjectPopupComponent,
        ProjectDeleteDialogComponent,
        ProjectDeletePopupComponent,
        ProjectCommiterPopupComponent,
        ProjectCommiterDialogComponent,
        ProjectAgentPopupComponent,
        ProjectAgentDialogComponent,
        FileSystemPopupComponent,
        FileSystemDialogComponent,
        ProjectRunPopupComponent,
        ProjectRunDialogComponent,
        SqlViewerComponent
    ],
    providers: [
        ProjectService,
        ProjectPopupService,
        SettingService,
        PagerService,
        SettingPopupService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SpongeProjectModule {}
