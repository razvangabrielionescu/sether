"use strict";
var Project = (function () {
    function Project(id, name, description, tool, spiders) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tool = tool;
        this.spiders = spiders;
    }
    return Project;
}());
exports.Project = Project;
//# sourceMappingURL=project.model.js.map