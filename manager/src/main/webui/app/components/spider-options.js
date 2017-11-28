import Ember from 'ember';
import SaveSpiderMixin from '../mixins/save-spider-mixin';

export default Ember.Component.extend(SaveSpiderMixin, {
    tagName: '',

    spider: null,
    authMethods: Ember.String.w('Form Basic Digest Ntlm Spnego Kerberos'),

    actions: {
        save() {
            this.saveSpider();
        },

        selectAuthMethod(method) {
            console.log('Selcted method: '+method);
            this.set('spider.authMethod', method);
        }
    }
});
