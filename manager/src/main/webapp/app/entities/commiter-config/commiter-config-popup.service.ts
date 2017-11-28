import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { CommiterConfig } from './commiter-config.model';
import { CommiterConfigService } from './commiter-config.service';

@Injectable()
export class CommiterConfigPopupService {
    private isOpen = false;
    constructor(
        private modalService: NgbModal,
        private router: Router,
        private commiterConfigService: CommiterConfigService

    ) {}

    open(component: Component, id?: number | any): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (id) {
            this.commiterConfigService.find(id).subscribe((commiterConfig) => {
                this.commiterConfigModalRef(component, commiterConfig);
            });
        } else {
            return this.commiterConfigModalRef(component, new CommiterConfig());
        }
    }

    commiterConfigModalRef(component: Component, commiterConfig: CommiterConfig): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.commiterConfig = commiterConfig;
        modalRef.result.then((result) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        });
        return modalRef;
    }
}
