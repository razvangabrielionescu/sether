import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { JhiPaginationUtil } from 'ng-jhipster';

import { CommiterFieldMappingComponent } from './commiter-field-mapping.component';
import { CommiterFieldMappingDetailComponent } from './commiter-field-mapping-detail.component';
import { CommiterFieldMappingPopupComponent } from './commiter-field-mapping-dialog.component';
import { CommiterFieldMappingDeletePopupComponent } from './commiter-field-mapping-delete-dialog.component';

import { Principal } from '../../shared';

export const commiterFieldMappingRoute: Routes = [
    {
        path: 'commiter-field-mapping/config/:id',
        component: CommiterFieldMappingComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.commiterFieldMapping.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'commiter-field-mapping/:id',
        component: CommiterFieldMappingDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.commiterFieldMapping.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const commiterFieldMappingPopupRoute: Routes = [
    {
        path: 'commiter-field-mapping-new/:parentId',
        component: CommiterFieldMappingPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.commiterFieldMapping.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'commiter-field-mapping/:id/edit',
        component: CommiterFieldMappingPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.commiterFieldMapping.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'commiter-field-mapping/:id/delete',
        component: CommiterFieldMappingDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.commiterFieldMapping.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
