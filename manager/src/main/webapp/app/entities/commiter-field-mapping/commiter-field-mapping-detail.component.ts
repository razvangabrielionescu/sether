import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';

import { CommiterFieldMapping } from './commiter-field-mapping.model';
import { CommiterFieldMappingService } from './commiter-field-mapping.service';

@Component({
    selector: 'jhi-commiter-field-mapping-detail',
    templateUrl: './commiter-field-mapping-detail.component.html'
})
export class CommiterFieldMappingDetailComponent implements OnInit, OnDestroy {

    commiterFieldMapping: CommiterFieldMapping;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private commiterFieldMappingService: CommiterFieldMappingService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInCommiterFieldMappings();
    }

    load(id) {
        this.commiterFieldMappingService.find(id).subscribe((commiterFieldMapping) => {
            this.commiterFieldMapping = commiterFieldMapping;
        });
    }

    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInCommiterFieldMappings() {
        this.eventSubscriber = this.eventManager.subscribe(
            'commiterFieldMappingListModification',
            (response) => this.load(this.commiterFieldMapping.id)
        );
    }
}
