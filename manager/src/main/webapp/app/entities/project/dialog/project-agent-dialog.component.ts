import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';

import { Project } from '../project.model';
import { ProjectPopupService } from '../project-popup.service';
import { ProjectService } from '../project.service';
import {ResponseWrapper} from '../../../shared/index';
import {Observable} from 'rxjs/Observable';
import {Agent} from '../../agent/agent.model';
import {AgentService} from '../../agent/agent.service';

@Component({
    selector: 'jhi-project-agent-dialog',
    templateUrl: './project-agent-dialog.component.html'
})
export class ProjectAgentDialogComponent implements OnInit {

    project: Project;
    agents: Agent[];
    selectedAgentId: number;
    isSaving: boolean;

    constructor(
        private projectService: ProjectService,
        private agentService: AgentService,
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.loadAllAgents();
        this.isSaving = false;
    }

    loadAllAgents() {
        this.agentService.query().subscribe(
            (res: ResponseWrapper) => {
                this.agents = res.json;
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    updateProject() {
        this.isSaving = true;
        this.subscribeToSaveResponse(
            this.projectService.update(this.project));
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

    agentFormSubmit(id: number) {
        this.project.agent =
            this.getAgent();
        this.updateProject();
    }

    getAgent() {
        for (const agent of this.agents) {
            if (agent.id == this.selectedAgentId) {
                return agent;
            }
        }

        return null;
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}

@Component({
    selector: 'jhi-project-agent-popup',
    template: ''
})
export class ProjectAgentPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private projectPopupService: ProjectPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.projectPopupService
                .open(ProjectAgentDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
