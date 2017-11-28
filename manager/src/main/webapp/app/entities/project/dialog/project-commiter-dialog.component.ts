import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';

import { Project } from '../project.model';
import { ProjectPopupService } from '../project-popup.service';
import { ProjectService } from '../project.service';
import {CommiterConfig} from '../../commiter-config/commiter-config.model';
import {CommiterConfigService} from '../../commiter-config/commiter-config.service';
import {ResponseWrapper} from '../../../shared/index';
import {Observable} from 'rxjs/Observable';

@Component({
    selector: 'jhi-project-commiter-dialog',
    templateUrl: './project-commiter-dialog.component.html'
})
export class ProjectCommiterDialogComponent implements OnInit {

    project: Project;
    commiterConfigs: CommiterConfig[];
    selectedCommiterConfigId: number;
    isSaving: boolean;

    constructor(
        private projectService: ProjectService,
        private commiterConfigService: CommiterConfigService,
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.loadAllCommiterConfigs();
        this.isSaving = false;
    }

    loadAllCommiterConfigs() {
        this.commiterConfigService.query().subscribe(
            (res: ResponseWrapper) => {
                this.commiterConfigs = res.json;
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

    commiterFormSubmit(id: number) {
        this.project.commiterConfig =
            this.getCommiterConfig();
        this.updateProject();
    }

    getCommiterConfig() {
        for (const config of this.commiterConfigs) {
            if (config.id == this.selectedCommiterConfigId) {
                return config;
            }
        }

        return null;
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}

@Component({
    selector: 'jhi-project-commiter-popup',
    template: ''
})
export class ProjectCommiterPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private projectPopupService: ProjectPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.projectPopupService
                .open(ProjectCommiterDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
