import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { SystemConfiguration } from './system-configuration.model';
import { SystemConfigurationService } from './system-configuration.service';

@Injectable()
export class SystemConfigurationPopupService {
    private isOpen = false;
    constructor(
        private modalService: NgbModal,
        private router: Router,
        private systemConfigurationService: SystemConfigurationService

    ) {}

    open(component: Component, id?: number | any): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (id) {
            this.systemConfigurationService.find(id).subscribe((systemConfiguration) => {
                this.systemConfigurationModalRef(component, systemConfiguration);
            });
        } else {
            return this.systemConfigurationModalRef(component, new SystemConfiguration());
        }
    }

    systemConfigurationModalRef(component: Component, systemConfiguration: SystemConfiguration): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.systemConfiguration = systemConfiguration;
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
