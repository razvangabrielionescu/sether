"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require('@angular/core');
var ProjectComponent = (function () {
    function ProjectComponent(projectService, alertService, eventManager, principal) {
        this.projectService = projectService;
        this.alertService = alertService;
        this.eventManager = eventManager;
        this.principal = principal;
    }
    ProjectComponent.prototype.loadAll = function () {
        var _this = this;
        this.projectService.query().subscribe(function (res) {
            _this.projects = res.json;
        }, function (res) { return _this.onError(res.json); });
    };
    ProjectComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.loadAll();
        this.principal.identity().then(function (account) {
            _this.currentAccount = account;
        });
        this.registerChangeInProjects();
    };
    ProjectComponent.prototype.ngOnDestroy = function () {
        this.eventManager.destroy(this.eventSubscriber);
    };
    ProjectComponent.prototype.trackId = function (index, item) {
        return item.id;
    };
    ProjectComponent.prototype.registerChangeInProjects = function () {
        var _this = this;
        this.eventSubscriber = this.eventManager.subscribe('projectListModification', function (response) { return _this.loadAll(); });
    };
    ProjectComponent.prototype.onError = function (error) {
        this.alertService.error(error.message, null, null);
    };
    ProjectComponent = __decorate([
        core_1.Component({
            selector: 'jhi-project',
            templateUrl: './project.component.html'
        })
    ], ProjectComponent);
    return ProjectComponent;
}());
exports.ProjectComponent = ProjectComponent;
//# sourceMappingURL=project.component.js.map