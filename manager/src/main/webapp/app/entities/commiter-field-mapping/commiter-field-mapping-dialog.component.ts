import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { CommiterFieldMapping } from './commiter-field-mapping.model';
import { CommiterFieldMappingPopupService } from './commiter-field-mapping-popup.service';
import { CommiterFieldMappingService } from './commiter-field-mapping.service';
import {ProjectService} from '../project/project.service';
import {ResponseWrapper} from '../../shared/model/response-wrapper.model';
import {CommiterConfigService} from '../commiter-config/commiter-config.service';
import {Commiter} from '../commiter/commiter.model';
import {OntologyProperty} from '../project/ontologyProperty.interface';
import {Project} from '../project/project.model';

@Component({
    selector: 'jhi-commiter-field-mapping-dialog',
    templateUrl: './commiter-field-mapping-dialog.component.html'
})
export class CommiterFieldMappingDialogComponent implements OnInit {
    commiterFieldMapping: CommiterFieldMapping;
    authorities: any[];
    isSaving: boolean;
    parentId: number;
    sourceFields: string[];
    destinationFields: OntologyProperty[];
    parentIsBigConnect: boolean;
    projectName: string;
    projects: string[] = [];

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private projectService: ProjectService,
        private commiterFieldMappingService: CommiterFieldMappingService,
        private commiterConfigService: CommiterConfigService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        this.loadAllProjects();
        this.loadSourceFields();
        this.determineBigConnect();
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    loadAllProjects() {
        this.projectService.query().subscribe(
            (res: ResponseWrapper) => {
                const projects: Project[] = res.json;
                this.projects = [];
                this.projects.push('');
                for (const p of projects) {
                    this.projects.push(p.name);
                }
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    projectChanged() {
        this.loadSourceFields();
    }

    loadSourceFields() {
        if (this.projectName == null ||
            this.projectName.trim() === '') {
            this.projectName = 'ALL';
        }
        this.projectService.findProjectFields(this.projectName).subscribe(
            (res: ResponseWrapper) => {
                this.sourceFields = res.json;
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    loadDestinationFields() {
        this.projectService.findOntologyFields(this.parentId).subscribe(
            (res: ResponseWrapper) => {
                this.destinationFields = res.json;
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    determineBigConnect() {
        this.commiterConfigService.find(this.parentId).subscribe((commiterConfig) => {
            const commiter: Commiter = commiterConfig.commiter;
            if (commiter.name === 'BigConnectCommitter') {
                this.parentIsBigConnect = true;
                this.loadDestinationFields();
            }
        });
    }

    save() {
        this.isSaving = true;
        if (this.commiterFieldMapping.id !== undefined) {
            this.subscribeToSaveResponse(
                this.commiterFieldMappingService.update(this.commiterFieldMapping));
        } else {
            this.subscribeToSaveResponse(
                this.commiterFieldMappingService.create(this.commiterFieldMapping, this.parentId));
        }
    }

    private subscribeToSaveResponse(result: Observable<CommiterFieldMapping>) {
        result.subscribe((res: CommiterFieldMapping) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: CommiterFieldMapping) {
        this.eventManager.broadcast({ name: 'commiterFieldMappingListModification', content: 'OK'});
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
    selector: 'jhi-commiter-field-mapping-popup',
    template: ''
})
export class CommiterFieldMappingPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private commiterFieldMappingPopupService: CommiterFieldMappingPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.commiterFieldMappingPopupService
                    .open(CommiterFieldMappingDialogComponent, params['id'], false);
            } else {
                this.modalRef = this.commiterFieldMappingPopupService
                    .open(CommiterFieldMappingDialogComponent, params['parentId'], true);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
