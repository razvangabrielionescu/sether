"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require('@angular/core');
var ProjectDetailComponent = (function () {
    function ProjectDetailComponent(eventManager, projectService, route) {
        this.eventManager = eventManager;
        this.projectService = projectService;
        this.route = route;
    }
    ProjectDetailComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.subscription = this.route.params.subscribe(function (params) {
            _this.load(params['id']);
        });
        this.registerChangeInProjects();
    };
    ProjectDetailComponent.prototype.load = function (id) {
        var _this = this;
        this.projectService.find(id).subscribe(function (project) {
            _this.project = project;
        });
    };
    ProjectDetailComponent.prototype.previousState = function () {
        window.history.back();
    };
    ProjectDetailComponent.prototype.ngOnDestroy = function () {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    };
    ProjectDetailComponent.prototype.registerChangeInProjects = function () {
        var _this = this;
        this.eventSubscriber = this.eventManager.subscribe('projectListModification', function (response) { return _this.load(_this.project.id); });
    };
    ProjectDetailComponent = __decorate([
        core_1.Component({
            selector: 'jhi-project-detail',
            templateUrl: './project-detail.component.html'
        })
    ], ProjectDetailComponent);
    return ProjectDetailComponent;
}());
exports.ProjectDetailComponent = ProjectDetailComponent;
//# sourceMappingURL=project-detail.component.js.map