import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';

import { CommiterConfig } from './commiter-config.model';
import { CommiterConfigService } from './commiter-config.service';

@Component({
    selector: 'jhi-commiter-config-detail',
    templateUrl: './commiter-config-detail.component.html'
})
export class CommiterConfigDetailComponent implements OnInit, OnDestroy {

    commiterConfig: CommiterConfig;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private commiterConfigService: CommiterConfigService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInCommiterConfigs();
    }

    load(id) {
        this.commiterConfigService.find(id).subscribe((commiterConfig) => {
            this.commiterConfig = commiterConfig;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInCommiterConfigs() {
        this.eventSubscriber = this.eventManager.subscribe(
            'commiterConfigListModification',
            (response) => this.load(this.commiterConfig.id)
        );
    }
}
