import Ember from 'ember';
import hasBrowserFeatures from '../utils/browser-features';
import config from '../config/environment';

function identity(x) { return x; }

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

    model() {
        return hasBrowserFeatures();
    },

    redirect(model) {
        let hasFeatures = model.every(identity);
        let nextRoute = hasFeatures ? 'projects' : 'browsers';
        this.replaceWith(nextRoute);
    }
});
