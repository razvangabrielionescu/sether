import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Setting } from './setting.model';
import { SettingPopupService } from './setting-popup.service';
import { SettingService } from './setting.service';

@Component({
    selector: 'jhi-setting-delete-dialog',
    templateUrl: './setting-delete-dialog.component.html'
})
export class SettingDeleteDialogComponent {

    setting: Setting;

    constructor(
        private settingService: SettingService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.settingService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'settingListModification',
                content: 'Deleted an setting'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-setting-delete-popup',
    template: ''
})
export class SettingDeletePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private settingPopupService: SettingPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.settingPopupService
                .open(SettingDeleteDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
