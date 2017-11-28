import { Routes } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { CommiterDetailComponent } from './commiter-detail.component';

export const commiterRoute: Routes = [{
        path: 'commiter/:id',
        component: CommiterDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'spongeApp.commiter.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];
