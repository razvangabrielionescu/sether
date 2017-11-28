"use strict";
var shared_1 = require('../../shared');
var project_component_1 = require('./project.component');
var project_detail_component_1 = require('./project-detail.component');
var project_dialog_component_1 = require('./project-dialog.component');
var project_delete_dialog_component_1 = require('./project-delete-dialog.component');
exports.projectRoute = [
    {
        path: 'project',
        component: project_component_1.ProjectComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.project.home.title'
        },
        canActivate: [shared_1.UserRouteAccessService]
    }, {
        path: 'project/:id',
        component: project_detail_component_1.ProjectDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.project.home.title'
        },
        canActivate: [shared_1.UserRouteAccessService]
    }
];
exports.projectPopupRoute = [
    {
        path: 'project-new',
        component: project_dialog_component_1.ProjectPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.project.home.title'
        },
        canActivate: [shared_1.UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'project/:id/edit',
        component: project_dialog_component_1.ProjectPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.project.home.title'
        },
        canActivate: [shared_1.UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'project/:id/delete',
        component: project_delete_dialog_component_1.ProjectDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.project.home.title'
        },
        canActivate: [shared_1.UserRouteAccessService],
        outlet: 'popup'
    }
];
//# sourceMappingURL=project.route.js.map