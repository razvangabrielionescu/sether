import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { CommiterFieldMapping } from './commiter-field-mapping.model';
import { CommiterFieldMappingService } from './commiter-field-mapping.service';

@Injectable()
export class CommiterFieldMappingPopupService {
    private isOpen = false;
    constructor(
        private modalService: NgbModal,
        private router: Router,
        private commiterFieldMappingService: CommiterFieldMappingService

    ) {}

    open(component: Component, id?: number | any, parent?: boolean): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (id && !parent) {
            this.commiterFieldMappingService.find(id).subscribe((commiterFieldMapping) => {
                this.commiterFieldMappingModalRef(component, commiterFieldMapping);
            });
        } else {
            return this.commiterFieldMappingModalRef(component, new CommiterFieldMapping(), id);
        }
    }

    commiterFieldMappingModalRef(component: Component, commiterFieldMapping: CommiterFieldMapping, parentId?: number): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.commiterFieldMapping = commiterFieldMapping;
        modalRef.componentInstance.parentId = parentId;
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
