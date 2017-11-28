import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { CommiterConfig } from './commiter-config.model';
import { CommiterConfigPopupService } from './commiter-config-popup.service';
import { CommiterConfigService } from './commiter-config.service';
import { Commiter, CommiterService } from '../commiter';
import { ResponseWrapper } from '../../shared';

@Component({
    selector: 'jhi-commiter-config-dialog',
    templateUrl: './commiter-config-dialog.component.html'
})
export class CommiterConfigDialogComponent implements OnInit {

    commiterConfig: CommiterConfig;
    authorities: any[];
    isSaving: boolean;

    commiters: Commiter[];

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private commiterConfigService: CommiterConfigService,
        private commiterService: CommiterService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        this.commiterService.query()
            .subscribe((res: ResponseWrapper) => { this.commiters = res.json; }, (res: ResponseWrapper) => this.onError(res.json));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.commiterConfig.id !== undefined) {
            this.subscribeToSaveResponse(
                this.commiterConfigService.update(this.commiterConfig));
        } else {
            this.subscribeToSaveResponse(
                this.commiterConfigService.create(this.commiterConfig));
        }
    }

    private subscribeToSaveResponse(result: Observable<CommiterConfig>) {
        result.subscribe((res: CommiterConfig) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: CommiterConfig) {
        this.eventManager.broadcast({ name: 'commiterConfigListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError(error) {
        try {
            error.json();
        } catch (exception) {
            error.message = error.text();
        }
        this.isSaving = false;
        this.onError(error);
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }

    trackCommiterById(index: number, item: Commiter) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-commiter-config-popup',
    template: ''
})
export class CommiterConfigPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private commiterConfigPopupService: CommiterConfigPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.commiterConfigPopupService
                    .open(CommiterConfigDialogComponent, params['id']);
            } else {
                this.modalRef = this.commiterConfigPopupService
                    .open(CommiterConfigDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
