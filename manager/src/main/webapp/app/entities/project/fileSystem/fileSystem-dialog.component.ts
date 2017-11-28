import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';

import { ProjectPopupService } from '../project-popup.service';
import { ProjectService } from '../project.service';
import {Project} from '../project.model';
import {Observable} from 'rxjs/Observable';

@Component({
    selector: 'jhi-file-system-dialog',
    templateUrl: './fileSystem-dialog.component.html'
})
export class FileSystemDialogComponent implements OnInit {
    isSaving: boolean;
    project: Project;

    constructor(
        private projectService: ProjectService,
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    create() {
        this.isSaving = true;
        this.subscribeToSaveResponse(
            this.projectService.createFS(this.project));
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
    selector: 'jhi-file-system-popup',
    template: ''
})
export class FileSystemPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private projectPopupService: ProjectPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.projectPopupService
                .open(FileSystemDialogComponent);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
