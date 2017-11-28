import Ember from 'ember';
import config from '../config/environment';

export default Ember.Service.extend({
    ajax: Ember.inject.service(),

    fetchCapabilities: Ember.on('init', function() {
        this.get('ajax')
            .request(config.appPrefix + '/' + config.modulePrefix + '/server_capabilities')
            .then(capabilities => {
            this.setProperties(capabilities);
        }, () => {
            Ember.run.later(this, this.fetchCapabilities, 5000);
        });
    })
});
