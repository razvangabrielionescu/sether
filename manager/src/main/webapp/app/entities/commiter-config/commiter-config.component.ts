import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager, JhiParseLinks, JhiPaginationUtil, JhiLanguageService, JhiAlertService } from 'ng-jhipster';

import { CommiterConfig } from './commiter-config.model';
import { CommiterConfigService } from './commiter-config.service';
import { ITEMS_PER_PAGE, Principal, ResponseWrapper } from '../../shared';
import { PaginationConfig } from '../../blocks/config/uib-pagination.config';

@Component({
    selector: 'jhi-commiter-config',
    templateUrl: './commiter-config.component.html'
})
export class CommiterConfigComponent implements OnInit, OnDestroy {
commiterConfigs: CommiterConfig[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        private commiterConfigService: CommiterConfigService,
        private alertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private principal: Principal
    ) {
    }

    loadAll() {
        this.commiterConfigService.query().subscribe(
            (res: ResponseWrapper) => {
                this.commiterConfigs = res.json;
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInCommiterConfigs();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: CommiterConfig) {
        return item.id;
    }

    registerChangeInCommiterConfigs() {
        this.eventSubscriber = this.eventManager.subscribe('commiterConfigListModification', (response) => this.loadAll());
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}
