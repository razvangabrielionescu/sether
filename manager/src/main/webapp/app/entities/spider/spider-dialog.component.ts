import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Spider } from './spider.model';
import { SpiderPopupService } from './spider-popup.service';
import { SpiderService } from './spider.service';
import { Project, ProjectService } from '../project';
import { ResponseWrapper } from '../../shared';

@Component({
    selector: 'jhi-spider-dialog',
    templateUrl: './spider-dialog.component.html'
})
export class SpiderDialogComponent implements OnInit {

    spider: Spider;
    authorities: any[];
    isSaving: boolean;

    projects: Project[];

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private spiderService: SpiderService,
        private projectService: ProjectService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        this.projectService.query()
            .subscribe((res: ResponseWrapper) => { this.projects = res.json; }, (res: ResponseWrapper) => this.onError(res.json));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.spider.id !== undefined) {
            this.subscribeToSaveResponse(
                this.spiderService.update(this.spider));
        } else {
            this.subscribeToSaveResponse(
                this.spiderService.create(this.spider));
        }
    }

    private subscribeToSaveResponse(result: Observable<Spider>) {
        result.subscribe((res: Spider) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: Spider) {
        this.eventManager.broadcast({ name: 'spiderListModification', content: 'OK'});
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

    trackProjectById(index: number, item: Project) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-spider-popup',
    template: ''
})
export class SpiderPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private spiderPopupService: SpiderPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.spiderPopupService
                    .open(SpiderDialogComponent, params['id']);
            } else {
                this.modalRef = this.spiderPopupService
                    .open(SpiderDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
