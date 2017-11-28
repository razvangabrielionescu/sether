import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';

import { Project } from '../project.model';
import { ProjectPopupService } from '../project-popup.service';
import { ProjectService } from '../project.service';

@Component({
    selector: 'jhi-project-run-dialog',
    templateUrl: './project-run-dialog.component.html'
})
export class ProjectRunDialogComponent implements OnInit {

    project: Project;
    runMode: string;
    period: number;
    unit: string;

    constructor(
        private projectService: ProjectService,
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.runMode = 'NOW';
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    runProject() {
        this.activeModal.dismiss('cancel');
        this.eventManager.broadcast({ name: 'projectStarted', content: this.project.id});

        if (this.runMode === 'NOW') {
            console.log('Starting project: ' + this.project.name);
            this.projectService.runProject(this.project).subscribe((res: Project) => {
                console.log('WebUiProject started: ' + this.project.name);
            });
        } else {
            console.log('Starting project scheduled: ' + this.project.name);
            this.projectService.runProjectScheduled(this.project, this.period, this.unit).subscribe((res: Project) => {
                console.log('WebUiProject started scheduled: ' + this.project.name);
            });
        }
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}

@Component({
    selector: 'jhi-project-run-popup',
    template: ''
})
export class ProjectRunPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private projectPopupService: ProjectPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.projectPopupService
                .open(ProjectRunDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
