import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';

import { ProjectComponent } from './project.component';
import { ProjectDetailComponent } from './project-detail.component';
import { ProjectPopupComponent } from './project-dialog.component';
import { ProjectDeletePopupComponent } from './project-delete-dialog.component';

import {ProjectCommiterPopupComponent} from './dialog/project-commiter-dialog.component';
import {ProjectAgentPopupComponent} from './dialog/project-agent-dialog.component';
import {ProjectRunPopupComponent} from './dialog/project-run-dialog.component';
import {SettingPopupComponent} from '../setting/setting-dialog.component';
import {SqlViewerComponent} from './sqlviewer/sqlviewer.component';
import {FileSystemPopupComponent} from './fileSystem/fileSystem-dialog.component';

export const projectRoute: Routes = [
    {
        path: 'project',
        component: ProjectComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.project.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'project/:id',
        component: ProjectDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.project.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'sqlviewer/:id',
        component: SqlViewerComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.project.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const projectPopupRoute: Routes = [
    {
        path: 'project-new',
        component: ProjectPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.project.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'project/:id/edit',
        component: ProjectPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.project.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'project/:id/delete',
        component: ProjectDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.project.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'project/:id/commiter',
        component: ProjectCommiterPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.project.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'project/:id/agent',
        component: ProjectAgentPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.project.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'project/:id/run',
        component: ProjectRunPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.project.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'fileSystem-new',
        component: FileSystemPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.project.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
