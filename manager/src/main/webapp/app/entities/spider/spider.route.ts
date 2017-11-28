import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { JhiPaginationUtil } from 'ng-jhipster';

import { SpiderComponent } from './spider.component';
import { SpiderDetailComponent } from './spider-detail.component';
import { SpiderPopupComponent } from './spider-dialog.component';
import { SpiderDeletePopupComponent } from './spider-delete-dialog.component';

import { Principal } from '../../shared';

export const spiderRoute: Routes = [
    {
        path: 'spider',
        component: SpiderComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.spider.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'spider/:id',
        component: SpiderDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.spider.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const spiderPopupRoute: Routes = [
    {
        path: 'spider-new',
        component: SpiderPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.spider.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'spider/:id/edit',
        component: SpiderPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.spider.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'spider/:id/delete',
        component: SpiderDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.spider.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
