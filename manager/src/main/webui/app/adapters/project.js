import ApplicationAdapter from './application';

export default ApplicationAdapter.extend({
    urlTemplate: '{+host}/sponge/webui/api/projects{/id}',
    findRecordUrlTemplate: '{+host}/sponge/webui/api/projects{/id}',
    createRecordUrlTemplate: '{+host}/sponge/webui/api/projects',
    shouldReloadRecord() { return true; }
});
