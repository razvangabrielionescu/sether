import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';

import { Spider } from './spider.model';
import { SpiderService } from './spider.service';

@Component({
    selector: 'jhi-spider-detail',
    templateUrl: './spider-detail.component.html'
})
export class SpiderDetailComponent implements OnInit, OnDestroy {

    spider: Spider;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private spiderService: SpiderService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInSpiders();
    }

    load(id) {
        this.spiderService.find(id).subscribe((spider) => {
            this.spider = spider;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInSpiders() {
        this.eventSubscriber = this.eventManager.subscribe(
            'spiderListModification',
            (response) => this.load(this.spider.id)
        );
    }
}
