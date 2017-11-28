"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require('@angular/core');
var ProjectDeleteDialogComponent = (function () {
    function ProjectDeleteDialogComponent(projectService, activeModal, eventManager) {
        this.projectService = projectService;
        this.activeModal = activeModal;
        this.eventManager = eventManager;
    }
    ProjectDeleteDialogComponent.prototype.clear = function () {
        this.activeModal.dismiss('cancel');
    };
    ProjectDeleteDialogComponent.prototype.confirmDelete = function (id) {
        var _this = this;
        this.projectService.delete(id).subscribe(function (response) {
            _this.eventManager.broadcast({
                name: 'projectListModification',
                content: 'Deleted an project'
            });
            _this.activeModal.dismiss(true);
        });
    };
    ProjectDeleteDialogComponent = __decorate([
        core_1.Component({
            selector: 'jhi-project-delete-dialog',
            templateUrl: './project-delete-dialog.component.html'
        })
    ], ProjectDeleteDialogComponent);
    return ProjectDeleteDialogComponent;
}());
exports.ProjectDeleteDialogComponent = ProjectDeleteDialogComponent;
var ProjectDeletePopupComponent = (function () {
    function ProjectDeletePopupComponent(route, projectPopupService) {
        this.route = route;
        this.projectPopupService = projectPopupService;
    }
    ProjectDeletePopupComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.routeSub = this.route.params.subscribe(function (params) {
            _this.modalRef = _this.projectPopupService
                .open(ProjectDeleteDialogComponent, params['id']);
        });
    };
    ProjectDeletePopupComponent.prototype.ngOnDestroy = function () {
        this.routeSub.unsubscribe();
    };
    ProjectDeletePopupComponent = __decorate([
        core_1.Component({
            selector: 'jhi-project-delete-popup',
            template: ''
        })
    ], ProjectDeletePopupComponent);
    return ProjectDeletePopupComponent;
}());
exports.ProjectDeletePopupComponent = ProjectDeletePopupComponent;
//# sourceMappingURL=project-delete-dialog.component.js.map