import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';

import { CommiterConfigComponent } from './commiter-config.component';
import { CommiterConfigDetailComponent } from './commiter-config-detail.component';
import { CommiterConfigPopupComponent } from './commiter-config-dialog.component';
import { CommiterConfigDeletePopupComponent } from './commiter-config-delete-dialog.component';
import {CommiterConfigClonePopupComponent} from './commiter-config-clone-dialog.component';

export const commiterConfigRoute: Routes = [
    {
        path: 'commiter-config',
        component: CommiterConfigComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.commiterConfig.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'commiter-config/:id',
        component: CommiterConfigDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.commiterConfig.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const commiterConfigPopupRoute: Routes = [
    {
        path: 'commiter-config-new',
        component: CommiterConfigPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.commiterConfig.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'commiter-config/:id/edit',
        component: CommiterConfigPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.commiterConfig.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'commiter-config/:id/delete',
        component: CommiterConfigDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.commiterConfig.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'commiter-config/:id/clone',
        component: CommiterConfigClonePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.commiterConfig.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
