import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Spider } from './spider.model';
import { SpiderPopupService } from './spider-popup.service';
import { SpiderService } from './spider.service';

@Component({
    selector: 'jhi-spider-delete-dialog',
    templateUrl: './spider-delete-dialog.component.html'
})
export class SpiderDeleteDialogComponent {

    spider: Spider;

    constructor(
        private spiderService: SpiderService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.spiderService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'spiderListModification',
                content: 'Deleted an spider'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-spider-delete-popup',
    template: ''
})
export class SpiderDeletePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private spiderPopupService: SpiderPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.spiderPopupService
                .open(SpiderDeleteDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
