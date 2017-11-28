import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { CommiterConfig } from './commiter-config.model';
import { CommiterConfigPopupService } from './commiter-config-popup.service';
import { CommiterConfigService } from './commiter-config.service';

@Component({
    selector: 'jhi-commiter-config-delete-dialog',
    templateUrl: './commiter-config-delete-dialog.component.html'
})
export class CommiterConfigDeleteDialogComponent {

    commiterConfig: CommiterConfig;

    constructor(
        private commiterConfigService: CommiterConfigService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.commiterConfigService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'commiterConfigListModification',
                content: 'Deleted an commiterConfig'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-commiter-config-delete-popup',
    template: ''
})
export class CommiterConfigDeletePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private commiterConfigPopupService: CommiterConfigPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.commiterConfigPopupService
                .open(CommiterConfigDeleteDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
