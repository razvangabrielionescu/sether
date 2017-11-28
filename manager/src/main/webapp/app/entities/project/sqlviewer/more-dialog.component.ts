import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import {TableCell} from './tableCell.interface';

@Component({
    selector: 'jhi-more-dialog',
    templateUrl: './more-dialog.component.html'
})
export class MoreDialogComponent {

    cell: TableCell;

    constructor(
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }
}
