import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager, JhiParseLinks, JhiPaginationUtil, JhiLanguageService, JhiAlertService } from 'ng-jhipster';

import { SystemConfiguration } from './system-configuration.model';
import { SystemConfigurationService } from './system-configuration.service';
import { ITEMS_PER_PAGE, Principal, ResponseWrapper } from '../../shared';
import { PaginationConfig } from '../../blocks/config/uib-pagination.config';

@Component({
    selector: 'jhi-system-configuration',
    templateUrl: './system-configuration.component.html'
})
export class SystemConfigurationComponent implements OnInit, OnDestroy {
systemConfigurations: SystemConfiguration[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        private systemConfigurationService: SystemConfigurationService,
        private alertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private principal: Principal
    ) {
    }

    loadAll() {
        this.systemConfigurationService.query().subscribe(
            (res: ResponseWrapper) => {
                this.systemConfigurations = res.json;
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInSystemConfigurations();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: SystemConfiguration) {
        return item.id;
    }
    registerChangeInSystemConfigurations() {
        this.eventSubscriber = this.eventManager.subscribe('systemConfigurationListModification', (response) => this.loadAll());
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}
