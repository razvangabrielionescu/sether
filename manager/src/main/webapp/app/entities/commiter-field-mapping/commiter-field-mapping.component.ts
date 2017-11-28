import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager, JhiParseLinks, JhiPaginationUtil, JhiLanguageService, JhiAlertService } from 'ng-jhipster';

import { CommiterFieldMapping } from './commiter-field-mapping.model';
import { CommiterFieldMappingService } from './commiter-field-mapping.service';
import { ITEMS_PER_PAGE, Principal, ResponseWrapper } from '../../shared';
import { PaginationConfig } from '../../blocks/config/uib-pagination.config';
import {CommiterConfigService} from '../commiter-config/commiter-config.service';
import {CommiterConfig} from '../commiter-config/commiter-config.model';

@Component({
    selector: 'jhi-commiter-field-mapping',
    templateUrl: './commiter-field-mapping.component.html'
})
export class CommiterFieldMappingComponent implements OnInit, OnDestroy {
    commiterFieldMappings: CommiterFieldMapping[];
    currentAccount: any;
    eventSubscriber: Subscription;
    private subscription: Subscription;
    parentId: number;
    parent: CommiterConfig;

    constructor(
        private commiterFieldMappingService: CommiterFieldMappingService,
        private commiterConfigService: CommiterConfigService,
        private alertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private route: ActivatedRoute,
        private principal: Principal
    ) {
    }

    loadAllForConfig(id) {
        this.commiterFieldMappingService.findAllForConfig(id).subscribe(
            (res: ResponseWrapper) => {
                this.commiterFieldMappings = res.json;
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    loadParent(id) {
        this.commiterConfigService.find(id).subscribe((commiterConfig) => {
            this.parent = commiterConfig;
        });
    }

    ngOnInit() {
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.subscription = this.route.params.subscribe((params) => {
            this.parentId = params['id'];
            this.loadParent(this.parentId);
            this.loadAllForConfig(this.parentId);
            this.registerChangeInCommiterFieldMappings(this.parentId);
        });
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: CommiterFieldMapping) {
        return item.id;
    }

    registerChangeInCommiterFieldMappings(id) {
        this.eventSubscriber = this.eventManager.subscribe('commiterFieldMappingListModification', (response) => this.loadAllForConfig(id));
    }

    previousState() {
        window.history.back();
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}
