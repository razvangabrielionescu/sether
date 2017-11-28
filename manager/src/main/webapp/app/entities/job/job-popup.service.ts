import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Job } from './job.model';
import { JobService } from './job.service';

@Injectable()
export class JobPopupService {
    private isOpen = false;
    constructor(
        private modalService: NgbModal,
        private router: Router,
        private jobService: JobService

    ) {}

    open(component: Component, id?: number | any): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (id) {
            this.jobService.find(id).subscribe((job) => {
                if (job.startTime) {
                    job.startTime = {
                        year: job.startTime.getFullYear(),
                        month: job.startTime.getMonth() + 1,
                        day: job.startTime.getDate()
                    };
                }
                if (job.endTime) {
                    job.endTime = {
                        year: job.endTime.getFullYear(),
                        month: job.endTime.getMonth() + 1,
                        day: job.endTime.getDate()
                    };
                }
                this.jobModalRef(component, job);
            });
        } else {
            return this.jobModalRef(component, new Job());
        }
    }

    jobModalRef(component: Component, job: Job): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.job = job;
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
