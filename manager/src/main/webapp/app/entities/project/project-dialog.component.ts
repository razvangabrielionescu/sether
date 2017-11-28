import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Project } from './project.model';
import { ProjectPopupService } from './project-popup.service';
import { ProjectService } from './project.service';
import {ResponseWrapper} from '../../shared/model/response-wrapper.model';
import {CommiterConfigService} from '../commiter-config/commiter-config.service';
import {CommiterConfig} from '../commiter-config/commiter-config.model';
import {Agent} from '../agent/agent.model';
import {AgentService} from '../agent/agent.service';

@Component({
    selector: 'jhi-project-dialog',
    templateUrl: './project-dialog.component.html'
})
export class ProjectDialogComponent implements OnInit {
    project: Project;
    authorities: any[];
    isSaving: boolean;
    commiterConfigs: CommiterConfig[];
    agents: Agent[];

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private projectService: ProjectService,
        private commiterConfigService: CommiterConfigService,
        private agentService: AgentService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        this.loadAllCommiterConfigs();
        this.loadAllAgents();
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.project.id !== undefined) {
            this.subscribeToSaveResponse(
                this.projectService.update(this.project));
        } else {
            this.subscribeToSaveResponse(
                this.projectService.create(this.project));
        }
    }

    loadAllCommiterConfigs() {
        this.commiterConfigService.query().subscribe(
            (res: ResponseWrapper) => {
                this.commiterConfigs = res.json;
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    loadAllAgents() {
        this.agentService.query().subscribe(
            (res: ResponseWrapper) => {
                this.agents = res.json;
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    private subscribeToSaveResponse(result: Observable<Project>) {
        result.subscribe((res: Project) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: Project) {
        this.eventManager.broadcast({ name: 'projectListModification', content: 'OK'});
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
    selector: 'jhi-project-popup',
    template: ''
})
export class ProjectPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private projectPopupService: ProjectPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.projectPopupService
                    .open(ProjectDialogComponent, params['id']);
            } else {
                this.modalRef = this.projectPopupService
                    .open(ProjectDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
