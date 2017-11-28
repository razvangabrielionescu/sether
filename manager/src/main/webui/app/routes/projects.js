import Ember from 'ember';
import config from '../config/environment';

export default Ember.Route.extend({
    ajax: Ember.inject.service(),

    beforeModel() {
        this.get('ajax').request(config.appPrefix+'/sapi/account').then(() => {
            //Ok
        }, () => {
            this.performLogin();
        });
    },

    performLogin() {
        window.location.href = '..#/webui-login';
    },
});
