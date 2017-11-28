import { Component, OnInit, OnDestroy } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';
import { Account, Principal } from '../../shared';
import { Project } from './project.model';
import { ProjectService } from './project.service';
import {SystemConfiguration} from '../system-configuration/system-configuration.model';

@Component({
    selector: 'jhi-project-detail',
    templateUrl: './project-detail.component.html'
})
export class ProjectDetailComponent implements OnInit, OnDestroy {
    account: Account;
    project: Project;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private principal: Principal,
        private eventManager: JhiEventManager,
        private projectService: ProjectService,
        private route: ActivatedRoute,
        private router: Router
    ) {
    }

    ngOnInit() {
        this.principal.identity().then((account) => {
            this.account = account;
        });
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInProjects();
    }

    load(id) {
        this.projectService.find(id).subscribe((project) => {
            this.project = project;
        });
    }

    previousState() {
        window.history.back();
    }

    removeSchedule() {
        this.projectService.removeProjectSchedule(this.project).subscribe((res: Project) => {
            console.log('WebUiProject schedule removed: ' + this.project.name);
            this.previousState();
        });
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInProjects() {
        this.eventSubscriber = this.eventManager.subscribe(
            'projectListModification',
            (response) => this.load(this.project.id)
        );
    }

    updateWebUi() {
        window.open('/sponge/webui/index.html#/projects/' + this.project.webUiProjectId, '_blank');
    }

    updateSocial() {
        this.projectService.getSocialUrl().subscribe((systemConfig) => {
            const config: SystemConfiguration = systemConfig;
            console.log('Updating social at location: ' + config.configValue);

            window.open(config.configValue + '/edit/' + this.project.name, '_blank');
        });
    }
}
