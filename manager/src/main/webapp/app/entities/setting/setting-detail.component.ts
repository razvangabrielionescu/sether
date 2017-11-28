import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager  } from 'ng-jhipster';

import { Setting } from './setting.model';
import { SettingService } from './setting.service';

@Component({
    selector: 'jhi-setting-detail',
    templateUrl: './setting-detail.component.html'
})
export class SettingDetailComponent implements OnInit, OnDestroy {

    setting: Setting;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private settingService: SettingService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInSettings();
    }

    load(id) {
        this.settingService.find(id).subscribe((setting) => {
            this.setting = setting;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInSettings() {
        this.eventSubscriber = this.eventManager.subscribe(
            'settingListModification',
            (response) => this.load(this.setting.id)
        );
    }
}
