import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { JhiPaginationUtil } from 'ng-jhipster';

import { SystemConfigurationComponent } from './system-configuration.component';
import { SystemConfigurationDetailComponent } from './system-configuration-detail.component';
import { SystemConfigurationPopupComponent } from './system-configuration-dialog.component';
import { SystemConfigurationDeletePopupComponent } from './system-configuration-delete-dialog.component';

import { Principal } from '../../shared';

export const systemConfigurationRoute: Routes = [
    {
        path: 'system-configuration',
        component: SystemConfigurationComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.systemConfiguration.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'system-configuration/:id',
        component: SystemConfigurationDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.systemConfiguration.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const systemConfigurationPopupRoute: Routes = [
    {
        path: 'system-configuration-new',
        component: SystemConfigurationPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.systemConfiguration.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'system-configuration/:id/edit',
        component: SystemConfigurationPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.systemConfiguration.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'system-configuration/:id/delete',
        component: SystemConfigurationDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.systemConfiguration.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
