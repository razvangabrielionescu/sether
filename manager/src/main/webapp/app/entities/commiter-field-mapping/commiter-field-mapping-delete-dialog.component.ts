import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { CommiterFieldMapping } from './commiter-field-mapping.model';
import { CommiterFieldMappingPopupService } from './commiter-field-mapping-popup.service';
import { CommiterFieldMappingService } from './commiter-field-mapping.service';

@Component({
    selector: 'jhi-commiter-field-mapping-delete-dialog',
    templateUrl: './commiter-field-mapping-delete-dialog.component.html'
})
export class CommiterFieldMappingDeleteDialogComponent {

    commiterFieldMapping: CommiterFieldMapping;

    constructor(
        private commiterFieldMappingService: CommiterFieldMappingService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.commiterFieldMappingService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'commiterFieldMappingListModification',
                content: 'Deleted an commiterFieldMapping'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-commiter-field-mapping-delete-popup',
    template: ''
})
export class CommiterFieldMappingDeletePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private commiterFieldMappingPopupService: CommiterFieldMappingPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.commiterFieldMappingPopupService
                .open(CommiterFieldMappingDeleteDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
