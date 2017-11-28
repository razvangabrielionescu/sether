import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { JhiPaginationUtil } from 'ng-jhipster';

import { SettingComponent } from './setting.component';
import { SettingDetailComponent } from './setting-detail.component';
import { SettingPopupComponent } from './setting-dialog.component';
import { SettingDeletePopupComponent } from './setting-delete-dialog.component';

import { Principal } from '../../shared';

export const settingRoute: Routes = [
    {
        path: 'setting',
        component: SettingComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.setting.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'setting/:id',
        component: SettingDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.setting.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const settingPopupRoute: Routes = [
    {
        path: 'setting-new',
        component: SettingPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.setting.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'setting/:id/edit',
        component: SettingPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.setting.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'setting/:id/delete',
        component: SettingDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.setting.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
