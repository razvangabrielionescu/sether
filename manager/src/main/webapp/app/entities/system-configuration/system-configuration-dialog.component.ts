import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { SystemConfiguration } from './system-configuration.model';
import { SystemConfigurationPopupService } from './system-configuration-popup.service';
import { SystemConfigurationService } from './system-configuration.service';

@Component({
    selector: 'jhi-system-configuration-dialog',
    templateUrl: './system-configuration-dialog.component.html'
})
export class SystemConfigurationDialogComponent implements OnInit {

    systemConfiguration: SystemConfiguration;
    authorities: any[];
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private systemConfigurationService: SystemConfigurationService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.systemConfiguration.id !== undefined) {
            this.subscribeToSaveResponse(
                this.systemConfigurationService.update(this.systemConfiguration));
        } else {
            this.subscribeToSaveResponse(
                this.systemConfigurationService.create(this.systemConfiguration));
        }
    }

    private subscribeToSaveResponse(result: Observable<SystemConfiguration>) {
        result.subscribe((res: SystemConfiguration) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: SystemConfiguration) {
        this.eventManager.broadcast({ name: 'systemConfigurationListModification', content: 'OK'});
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
}

@Component({
    selector: 'jhi-system-configuration-popup',
    template: ''
})
export class SystemConfigurationPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private systemConfigurationPopupService: SystemConfigurationPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.systemConfigurationPopupService
                    .open(SystemConfigurationDialogComponent, params['id']);
            } else {
                this.modalRef = this.systemConfigurationPopupService
                    .open(SystemConfigurationDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
