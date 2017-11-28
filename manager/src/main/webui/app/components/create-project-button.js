import Ember from 'ember';
import config from '../config/environment';
const { computed, inject: { service } } = Ember;

export default Ember.Component.extend({
    ajax: Ember.inject.service(),
    dispatcher: service(),
    capabilities: service(),
    tagName: '',
    notificationManager: service(),

    canCreateProjects: computed.readOnly('capabilities.capabilities.create_projects'),
    projectName: null,

    actions: {
        addProject() {
            const projectName = this.get('projectName');
            this.get('ajax')
                .request(
                    config.appPrefix +
                    '/' +
                    config.modulePrefix +
                    '/api/projects/unique/' +
                    projectName)
                .then((data) => {
                if (data === true) {
                    this.get('dispatcher').addProject(projectName, /* redirect = */true);
                } else {
                    this.get('notificationManager')
                        .showErrorNotification(
                            'Project with name ' + projectName + ' already exists');
                }
            }, () => {
                    this.get('notificationManager')
                        .showErrorNotification(
                            'Unexpected error. Please contact the administrator.');
            });
        }
    }
});
