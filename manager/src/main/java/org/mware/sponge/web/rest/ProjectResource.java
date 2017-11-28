package org.mware.sponge.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.mware.sponge.ProjectController;
import org.mware.sponge.domain.CommiterFieldMapping;
import org.mware.sponge.domain.Project;

import org.mware.sponge.repository.CommiterConfigRepository;
import org.mware.sponge.repository.ProjectRepository;
import org.mware.sponge.service.ProjectService;
import org.mware.sponge.service.dto.ProjectDTO;
import org.mware.sponge.service.dto.SpiderDTO;
import org.mware.sponge.service.dto.util.IJob;
import org.mware.sponge.service.dto.util.OntologyProperty;
import org.mware.sponge.util.Constants;
import org.mware.sponge.util.DatabaseHandler;
import org.mware.sponge.util.TableData;
import org.mware.sponge.util.TableInfo;
import org.mware.sponge.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing WebUiProject.
 */
@RestController
@RequestMapping("/sapi")
public class ProjectResource {

    private final Logger log = LoggerFactory.getLogger(ProjectResource.class);

    private static final String ENTITY_NAME = "project";

    private final ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CommiterConfigRepository commiterConfigRepository;

    @Autowired
    private ProjectController projectController;

    public ProjectResource(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * POST  /projects : Create a new project.
     *
     * @param project the project to create
     * @return the ResponseEntity with status 201 (Created) and with body the new project, or with status 400 (Bad Request) if the project has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/projects")
    @Timed
    public ResponseEntity<ProjectDTO> createProject(Principal principal, @RequestBody ProjectDTO project) throws URISyntaxException {
        log.debug("REST request to save Project : {}", project);
        if (project.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new project cannot already have an ID")).body(null);
        }
        Project result = projectService.saveProject(project, principal.getName());
        return ResponseEntity.created(new URI("/sapi/projects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(new ProjectDTO(result));
    }

    @PostMapping("/projects/fs")
    @Timed
    public ResponseEntity<ProjectDTO> createFileSystemProject(Principal principal, @RequestBody ProjectDTO project) throws URISyntaxException {
        log.debug("REST request to create File System Project : {}", project);
        if (project.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new project cannot already have an ID")).body(null);
        }
        project.setTool(Constants.TOOL_FILESYSTEM);
        Project result = projectService.createFileSystemProject(project, principal.getName(), projectController.getNorconnexBase());
        return ResponseEntity.created(new URI("/sapi/projects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(new ProjectDTO(result));
    }

    @PostMapping("/tables")
    @Timed
    public TableInfo getTableInfo(@RequestBody Project project) throws URISyntaxException {
        log.debug("REST request to get Table information for project : {}", project);
        if (project.getCommiterConfig() == null ||
                !project.getCommiterConfig().getCommiter().getName().equals(Constants.COMMITTER_DATABASE_NAME)) {
            log.error("Trying to get database info for a wrong committer configuration");
            return null;
        }

        DatabaseHandler dbHandler = new DatabaseHandler();
        dbHandler.loadFromCommiterConfig(project.getCommiterConfig());
        TableInfo tableInfo = null;
        try {
            tableInfo = dbHandler.getTableInfo();

            //Filter out only the current project field mapping tables
            List<String> allowedTableNames = new ArrayList<String>();
            String table = null;
            Set<CommiterFieldMapping> mappings =
                    commiterConfigRepository.findOne(project.getCommiterConfig().getId()).getCommiterFieldMappings();
            for (CommiterFieldMapping fieldMapping : mappings) {
                if (fieldMapping.getDestinationField().indexOf(".") > 0) {
                    table = fieldMapping.getDestinationField().split("\\.")[0];
                    if (!allowedTableNames.contains(table)) {
                        allowedTableNames.add(table);
                    }
                }
            }
            tableInfo.setTableNames(tableInfo.getTableNames().stream().filter(tableName -> {
                return allowedTableNames.contains(tableName);
            }).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tableInfo;
    }

    @PostMapping("/tableData")
    @Timed
    public TableData getTableData(@RequestBody ProjectDTO project) throws URISyntaxException {
        log.debug("REST request to get Table Data for table : {}", project.getTableName());
        if (project.getTableName() == null || project.getTableName().trim().isEmpty()) {
            log.error("Wrong table name");
            return null;
        }

        DatabaseHandler dbHandler = new DatabaseHandler();
        dbHandler.loadFromCommiterConfig(project.getCommiterConfig());
        TableData tableData = null;
        try {
            tableData = dbHandler.getTableData(project.getTableName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tableData;
    }

    /**
     * @param project
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/projects/control/start")
    @Timed
    public ProjectDTO startProject(@RequestBody ProjectDTO project) throws URISyntaxException {
        log.info("REST request to start project : {}", project);
        projectController.startProject(project);
        return project;
    }

    @PostMapping("/projects/control/start-scheduled")
    @Timed
    public ProjectDTO startProjectScheduled(@RequestBody ProjectDTO project) throws URISyntaxException {
        log.info("REST request to start project scheduled: {}", project);
        projectController.startProjectScheduled(project);
        return project;
    }

    @PostMapping("/projects/control/remove-schedule")
    @Timed
    public ProjectDTO removeProjectSchedule(@RequestBody ProjectDTO project) throws URISyntaxException {
        log.info("REST request to remove project schedule: {}", project);
        projectController.removeProjectSchedule(project);
        return project;
    }

    @PostMapping("/projects/control/stop")
    @Timed
    public ProjectDTO stopProject(@RequestBody ProjectDTO project) throws URISyntaxException {
        log.info("REST request to stop project : {}", project);
        projectController.stopProject(project);
        return project;
    }

    /**
     * PUT  /projects : Updates an existing project.
     *
     * @param project the project to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated project,
     * or with status 400 (Bad Request) if the project is not valid,
     * or with status 500 (Internal Server Error) if the project couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/projects")
    @Timed
    public ResponseEntity<ProjectDTO> updateProject(Principal principal, @RequestBody ProjectDTO project) throws URISyntaxException {
        log.info("REST request to update Project : {}", project);
        if (project.getId() == null) {
            return createProject(principal, project);
        }
        Project result = projectService.saveProject(project, principal.getName());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, project.getId().toString()))
            .body(new ProjectDTO(result));
    }

    /**
     * @return
     */
    @GetMapping("/projects")
    @Timed
    public List<ProjectDTO> getAllProjects(Principal principal) {
        log.trace("REST request to get all projects");
        if (principal == null) {
            log.warn("User was not authenticated");
            return null;
        }

        Map<String, ProjectController.ProjectStatus> statusMap = projectController.getProjectStatusMap();
        List<ProjectDTO> projects = projectService.findAllProjectsForUser(principal.getName());

        boolean projectDetermined = false;
        ProjectController.ProjectStatus projectStatus = null;
        for (ProjectDTO project : projects) {
            if (statusMap.containsKey(project.getName())) {
                projectStatus = statusMap.get(project.getName());
                copyStatus(project, projectStatus);
                projectDetermined = true;
            }
            final Set<SpiderDTO> spiders = project.getSpiders();
            boolean spiderDetermined = false;
            String determinedSpiderName = null;
            for (SpiderDTO spider : spiders) {
                if (statusMap.containsKey(spider.getName())) {
                    projectStatus = statusMap.get(spider.getName());
                    copyStatus(spider, projectStatus);
                    determinedSpiderName = spider.getName();
                    spiderDetermined = true;
                }
            };

            if (!projectDetermined && spiderDetermined) {
                for (SpiderDTO spider : spiders) {
                    if (spider.getName().equals(determinedSpiderName)) {
                        project.setStatus(spider.getStatus());
                    }
                }
            }
            projectDetermined = false;

            // Stop completed projects
            if (project.getStatus() != null &&
                    project.getStatus().getStatus().equals(Constants.NORCONNEX_COMPLETED_STATUS)) {
                projectController.stopProject(project);
            }
        }

        return projects;
    }

    @GetMapping("/project/fields/{name}")
    @Timed
    public List<String> getFieldsForProject(Principal principal, @PathVariable String name) {
        log.debug("REST request to get all source fields for user: "+principal.getName()+", and project: "+name);
        return projectService.findAllSourceFields(this.projectController.getNorconnexBase(), principal.getName(), name);
    }

    @GetMapping("/project/ontology/{id}")
    @Timed
    public List<OntologyProperty> getOntologyForCommitterConfig(@PathVariable Long id) {
        log.debug("REST request to get all ontology fields for committer config if: "+id);
        List<OntologyProperty> result = new ArrayList<OntologyProperty>();
        try {
            result = projectService.findBigConnectOntologyFieldsForCommitterConfig(id);
        } catch (IOException e) {
           log.error("There was an error while getting ontology fields with message: "+e.getMessage());
           e.printStackTrace();
        }

        return result;
    }

    private void copyStatus(IJob project, ProjectController.ProjectStatus projectStatus) {
        project.getStatus().setNote(projectStatus.getNote());
        project.getStatus().setProgress(projectStatus.getProgress());
        project.getStatus().setLastActivity(projectStatus.getLastActivity());
        project.getStatus().setStatus(projectStatus.getStatus());
    }

    /**
     * GET  /projects/:id : get the "id" project.
     *
     * @param id the id of the project to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the project, or with status 404 (Not Found)
     */
    @GetMapping("/projects/{id}")
    @Timed
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {
        log.debug("REST request to get WebUiProject : {}", id);
        Project project = projectRepository.findOne(id);
        if(project != null)
            return ResponseEntity.ok().body(new ProjectDTO(project));
        else
            return ResponseEntity.notFound().build();
    }

    /**
     * DELETE  /projects/:id : delete the "id" project.
     *
     * @param id the id of the project to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/projects/{id}")
    @Timed
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        log.debug("REST request to delete WebUiProject : {}", id);
        Project project = projectRepository.findOne(id);
        if(project != null) {
            projectController.deleteProject(project.getTool(), project.getName(), project.getUserName());
            projectRepository.delete(id);
        } else {
            log.warn("The project with id: "+id+" wasn't found to be deleted.");
        }

        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
