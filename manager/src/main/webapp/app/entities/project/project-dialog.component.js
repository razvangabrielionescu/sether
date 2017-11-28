"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require('@angular/core');
var ProjectDialogComponent = (function () {
    function ProjectDialogComponent(activeModal, alertService, projectService, eventManager) {
        this.activeModal = activeModal;
        this.alertService = alertService;
        this.projectService = projectService;
        this.eventManager = eventManager;
    }
    ProjectDialogComponent.prototype.ngOnInit = function () {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
    };
    ProjectDialogComponent.prototype.clear = function () {
        this.activeModal.dismiss('cancel');
    };
    ProjectDialogComponent.prototype.save = function () {
        this.isSaving = true;
        if (this.project.id !== undefined) {
            this.subscribeToSaveResponse(this.projectService.update(this.project));
        }
        else {
            this.subscribeToSaveResponse(this.projectService.create(this.project));
        }
    };
    ProjectDialogComponent.prototype.subscribeToSaveResponse = function (result) {
        var _this = this;
        result.subscribe(function (res) {
            return _this.onSaveSuccess(res);
        }, function (res) { return _this.onSaveError(res); });
    };
    ProjectDialogComponent.prototype.onSaveSuccess = function (result) {
        this.eventManager.broadcast({ name: 'projectListModification', content: 'OK' });
        this.isSaving = false;
        this.activeModal.dismiss(result);
    };
    ProjectDialogComponent.prototype.onSaveError = function (error) {
        try {
            error.json();
        }
        catch (exception) {
            error.message = error.text();
        }
        this.isSaving = false;
        this.onError(error);
    };
    ProjectDialogComponent.prototype.onError = function (error) {
        this.alertService.error(error.message, null, null);
    };
    ProjectDialogComponent = __decorate([
        core_1.Component({
            selector: 'jhi-project-dialog',
            templateUrl: './project-dialog.component.html'
        })
    ], ProjectDialogComponent);
    return ProjectDialogComponent;
}());
exports.ProjectDialogComponent = ProjectDialogComponent;
var ProjectPopupComponent = (function () {
    function ProjectPopupComponent(route, projectPopupService) {
        this.route = route;
        this.projectPopupService = projectPopupService;
    }
    ProjectPopupComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.routeSub = this.route.params.subscribe(function (params) {
            if (params['id']) {
                _this.modalRef = _this.projectPopupService
                    .open(ProjectDialogComponent, params['id']);
            }
            else {
                _this.modalRef = _this.projectPopupService
                    .open(ProjectDialogComponent);
            }
        });
    };
    ProjectPopupComponent.prototype.ngOnDestroy = function () {
        this.routeSub.unsubscribe();
    };
    ProjectPopupComponent = __decorate([
        core_1.Component({
            selector: 'jhi-project-popup',
            template: ''
        })
    ], ProjectPopupComponent);
    return ProjectPopupComponent;
}());
exports.ProjectPopupComponent = ProjectPopupComponent;
//# sourceMappingURL=project-dialog.component.js.map