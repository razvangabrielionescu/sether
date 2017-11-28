import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Setting } from './setting.model';
import { SettingPopupService } from './setting-popup.service';
import { SettingService } from './setting.service';
import { Project, ProjectService } from '../project';
import { ResponseWrapper } from '../../shared';

@Component({
    selector: 'jhi-setting-dialog',
    templateUrl: './setting-dialog.component.html'
})
export class SettingDialogComponent implements OnInit {

    setting: Setting;
    authorities: any[];
    isSaving: boolean;

    projects: Project[];

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private settingService: SettingService,
        private projectService: ProjectService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        this.projectService.query()
            .subscribe((res: ResponseWrapper) => { this.projects = res.json; }, (res: ResponseWrapper) => this.onError(res.json));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.setting.id !== undefined) {
            this.subscribeToSaveResponse(
                this.settingService.update(this.setting));
        } else {
            this.subscribeToSaveResponse(
                this.settingService.create(this.setting));
        }
    }

    private subscribeToSaveResponse(result: Observable<Setting>) {
        result.subscribe((res: Setting) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: Setting) {
        this.eventManager.broadcast({ name: 'settingListModification', content: 'OK'});
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

    trackProjectById(index: number, item: Project) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-setting-popup',
    template: ''
})
export class SettingPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private settingPopupService: SettingPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.settingPopupService
                    .open(SettingDialogComponent, params['id']);
            } else {
                this.modalRef = this.settingPopupService
                    .open(SettingDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
