import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Project } from './project.model';
import { ProjectService } from './project.service';
import { Principal, ResponseWrapper } from '../../shared';
import {SystemConfiguration} from '../system-configuration/system-configuration.model';
import {WindowRef} from '../../shared/tracker/window.service';

@Component({
    selector: 'jhi-project',
    templateUrl: './project.component.html',
    styleUrls: [
        'project.css'
    ]
})
export class ProjectComponent implements OnInit, OnDestroy {
    projects: Project[];
    currentAccount: any;
    eventSubscriber: Subscription;
    runSubscriber: Subscription;
    runLocal: string;

    constructor(
        private projectService: ProjectService,
        private alertService: JhiAlertService,
        private router: Router,
        private eventManager: JhiEventManager,
        private $window: WindowRef,
        private principal: Principal
    ) {
    }

    loadAll() {
        this.projectService.query().subscribe(
            (res: ResponseWrapper) => {
                this.syncShow(this.projects, res.json);
                this.projects = res.json;
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    syncShow(originalProjects: Project[], newProjects: Project[]) {
        if (originalProjects === undefined || newProjects === undefined) {
            return;
        }

        for (const oProject of originalProjects) {
            for (const nProject of newProjects) {
                if (oProject.name === nProject.name) {
                    nProject.show = oProject.show;
                    // May cause inconsistencies that can be fixed with page refresh (for ui responsiveness)
                    if (oProject.status.status === 'STOPPING' && nProject.status.status === 'RUNNING') {
                        nProject.status.status = 'STOPPING';
                    }
                    if (oProject.status.status === 'STARTING' && nProject.status.status === 'STOPPED') {
                        nProject.status.status = 'STARTING';
                    }
                }
            }
        }
    }

    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInProjects();
        this.registerRunSubscriber();

        this.projectService.subscribe();
        this.projectService.receive().subscribe((data) => {
            this.loadAll();
        });

        this.projectService.runCollectorsLocally().subscribe((systemConfig) => {
            const config: SystemConfiguration = systemConfig;
            console.log('Run collectors locally: ' + config.configValue);
            this.runLocal = config.configValue;
        });
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
        this.projectService.unsubscribe();
    }

    trackId(index: number, item: Project) {
        return item.id;
    }

    registerChangeInProjects() {
        this.eventSubscriber = this.eventManager.subscribe('projectListModification', (response) => this.loadAll());
    }

    registerRunSubscriber() {
        this.runSubscriber = this.eventManager.subscribe('projectStarted', (response) => this.projectStarted(response));
    }

    projectStarted(response) {
        for (const project of this.projects) {
            if (project.id === response.content) {
                project.status.status = 'STARTING';
            }
        }
    }

    openWebUi() {
        window.open('/sponge/webui/index.html', '_blank');
    }

    openSocial() {
        this.projectService.getSocialUrl().subscribe((systemConfig) => {
            const config: SystemConfiguration = systemConfig;
            console.log('Opening social at location: ' + config.configValue);

            window.open(config.configValue, '_blank');
        });
    }

    startProject(project: Project) {
        if (project.commiterConfig == null) {
            this.router.navigate([{ outlets: { popup: 'project/' + project.id + '/commiter' }}]);
            return;
        }
        if (project.agent == null && this.runLocal === 'false') {
            this.router.navigate([{ outlets: { popup: 'project/' + project.id + '/agent' }}]);
            return;
        }

        if (project.job == null) {
            this.router.navigate([{outlets: {popup: 'project/' + project.id + '/run'}}]);
        } else {
            console.log('Starting project: ' + project.name);
            project.status.status = 'STARTING';
            this.projectService.runProject(project).subscribe((res: Project) => {
                console.log('WebUiProject started: ' + project.name);
            });
        }
    }

    stopProject(project: Project) {
        console.log('Stopping project: ' + project.name);
        project.status.status = 'STOPPING';
        this.projectService.stopProject(project).subscribe((res: Project) => {
            console.log('WebUiProject stopped: ' + project.name);
        });
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}
