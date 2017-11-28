import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { CommiterConfig } from './commiter-config.model';
import { CommiterConfigPopupService } from './commiter-config-popup.service';
import { CommiterConfigService } from './commiter-config.service';

@Component({
    selector: 'jhi-commiter-config-clone-dialog',
    templateUrl: './commiter-config-clone-dialog.component.html'
})
export class CommiterConfigCloneDialogComponent implements OnInit {
    commiterConfig: CommiterConfig;
    name: string;
    cloneMapping: boolean;

    constructor(
        private commiterConfigService: CommiterConfigService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.name = '[CLONE] ' + this.commiterConfig.name;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmClone(id: number) {
        if (this.cloneMapping == undefined) {
            this.cloneMapping = false;
        }
        this.commiterConfigService.clone(id, this.name, this.cloneMapping).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'commiterConfigListModification',
                content: 'Cloned a commiterConfig'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-commiter-config-clone-popup',
    template: ''
})
export class CommiterConfigClonePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private commiterConfigPopupService: CommiterConfigPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.commiterConfigPopupService
                .open(CommiterConfigCloneDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
