import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';

import { Commiter } from './commiter.model';
import { CommiterService } from './commiter.service';

@Component({
    selector: 'jhi-commiter-detail',
    templateUrl: './commiter-detail.component.html'
})
export class CommiterDetailComponent implements OnInit, OnDestroy {

    commiter: Commiter;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private commiterService: CommiterService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInCommiters();
    }

    load(id) {
        this.commiterService.find(id).subscribe((commiter) => {
            this.commiter = commiter;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInCommiters() {
        this.eventSubscriber = this.eventManager.subscribe(
            'commiterListModification',
            (response) => this.load(this.commiter.id)
        );
    }
}
