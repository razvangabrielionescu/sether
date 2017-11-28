import { Routes } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import {WebuiLoginComponent} from './webui-login.component';

export const webuiLoginRoute: Routes = [
    {
        path: 'webui-login',
        component: WebuiLoginComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.agent.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];
