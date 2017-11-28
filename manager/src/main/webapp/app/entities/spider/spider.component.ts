import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager, JhiParseLinks, JhiPaginationUtil, JhiLanguageService, JhiAlertService } from 'ng-jhipster';

import { Spider } from './spider.model';
import { SpiderService } from './spider.service';
import { ITEMS_PER_PAGE, Principal, ResponseWrapper } from '../../shared';
import { PaginationConfig } from '../../blocks/config/uib-pagination.config';

@Component({
    selector: 'jhi-spider',
    templateUrl: './spider.component.html'
})
export class SpiderComponent implements OnInit, OnDestroy {
spiders: Spider[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        private spiderService: SpiderService,
        private alertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private principal: Principal
    ) {
    }

    loadAll() {
        this.spiderService.query().subscribe(
            (res: ResponseWrapper) => {
                this.spiders = res.json;
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInSpiders();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: Spider) {
        return item.id;
    }
    registerChangeInSpiders() {
        this.eventSubscriber = this.eventManager.subscribe('spiderListModification', (response) => this.loadAll());
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}
