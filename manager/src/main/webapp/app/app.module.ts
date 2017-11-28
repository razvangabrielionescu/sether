import './vendor.ts';

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { Ng2Webstorage } from 'ng2-webstorage';
import { RouterModule } from '@angular/router';

import { SpongeSharedModule, UserRouteAccessService } from './shared';
import { SpongeHomeModule } from './home/home.module';
import { SpongeAdminModule } from './admin/admin.module';
import { SpongeAccountModule } from './account/account.module';
import { SpongeEntityModule } from './entities/entity.module';

import { customHttpProvider } from './blocks/interceptor/http.provider';
import { PaginationConfig } from './blocks/config/uib-pagination.config';
import {Ng2PaginationModule} from 'ng2-pagination';
// jhipster-needle-angular-add-module-import JHipster will add new module here

import {
    JhiMainComponent,
    LayoutRoutingModule,
    NavbarComponent,
    FooterComponent,
    ProfileService,
    PageRibbonComponent,
    ActiveMenuDirective,
    ErrorComponent
} from './layouts';
import {OrderByPipe} from './entities/project/sqlviewer/orderby.pipe';
import {MoreDialogComponent} from './entities/project/sqlviewer/more-dialog.component';
import {WebuiLoginComponent} from './shared/webui-login/webui-login.component';
import {webuiLoginRoute} from './shared/webui-login/webui-login.route';

const ENTITY_STATES = [
    ...webuiLoginRoute
];

@NgModule({
    imports: [
        BrowserModule,
        LayoutRoutingModule,
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-'}),
        SpongeSharedModule,
        SpongeHomeModule,
        SpongeAdminModule,
        SpongeAccountModule,
        SpongeEntityModule,
        Ng2PaginationModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
        // jhipster-needle-angular-add-module JHipster will add new module here
    ],
    declarations: [
        JhiMainComponent,
        NavbarComponent,
        ErrorComponent,
        PageRibbonComponent,
        ActiveMenuDirective,
        FooterComponent,
        OrderByPipe,
        MoreDialogComponent,
        WebuiLoginComponent
    ],
    entryComponents: [
        MoreDialogComponent
    ],
    providers: [
        ProfileService,
        customHttpProvider(),
        PaginationConfig,
        UserRouteAccessService
    ],
    bootstrap: [ JhiMainComponent ]
})
export class SpongeAppModule {}
