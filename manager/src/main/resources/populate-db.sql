INSERT INTO jhi_authority(name) VALUES ('ROLE_ADMIN');
INSERT INTO jhi_authority(name) VALUES ('ROLE_USER');

INSERT INTO jhi_user(id, login, password_hash, first_name, last_name, email, activated, lang_key, created_by, last_modified_by) VALUES (1, 'system', '$2a$10$mE.qmcV0mFU5NcKh73TZx.z4ueI/.bDWbj0T1BYyqP481kGGarKLG', 'System', 'System', 'system@localhost', true, 'en', 'system', 'system');
INSERT INTO jhi_user(id, login, password_hash, first_name, last_name, email, activated, lang_key, created_by, last_modified_by) VALUES (2, 'anonymoususer', '$2a$10$j8S5d7Sr7.8VTOYNviDPOeWX8KcYILUVJBsYV83Y5NtECayypx9lO', 'Anonymous', 'User', 'anonymous@localhost', true, 'en', 'system', 'system');
INSERT INTO jhi_user(id, login, password_hash, first_name, last_name, email, activated, lang_key, created_by, last_modified_by) VALUES (3, 'admin', '$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC', 'Administrator', 'Administrator', 'admin@localhost', true, 'en', 'system', 'system');
INSERT INTO jhi_user(id, login, password_hash, first_name, last_name, email, activated, lang_key, created_by, last_modified_by) VALUES (4, 'user', '$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K', 'User', 'User', 'user@localhost', true, 'en', 'system', 'system');

INSERT INTO jhi_user_authority(user_id, authority_name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO jhi_user_authority(user_id, authority_name) VALUES (1, 'ROLE_USER');
INSERT INTO jhi_user_authority(user_id, authority_name) VALUES (3, 'ROLE_ADMIN');
INSERT INTO jhi_user_authority(user_id, authority_name) VALUES (3, 'ROLE_USER');
INSERT INTO jhi_user_authority(user_id, authority_name) VALUES (4, 'ROLE_USER');

INSERT INTO agent(id, name, host, port) VALUES (1, 'LocalAgent', 'localhost', 6789);

INSERT INTO commiter(id, name, clazz, description) VALUES (1, 'FileSystemCommitter', 'com.norconex.committer.core.impl.FileSystemCommitter', 'Commits documents on the filesystem in a format used by Collectors or other Committers');
INSERT INTO commiter(id, name, clazz, description) VALUES (2, 'JSONFileCommitter', 'com.norconex.committer.core.impl.JSONFileCommitter', 'Commits documents to JSON files');
INSERT INTO commiter(id, name, clazz, description) VALUES (3, 'XMLFileCommitter', 'com.norconex.committer.core.impl.XMLFileCommitter', 'Commits documents to XML files');
INSERT INTO commiter(id, name, clazz, description) VALUES (4, 'SQLCommitter', 'org.mware.sponge.crawl.committer.SQLCommitter', 'Commits to SQL databases files');
INSERT INTO commiter(id, name, clazz, description) VALUES (5, 'BigConnectCommitter', 'org.mware.sponge.crawl.committer.BigConnectCommitter', 'Commits in BigConnect');

INSERT INTO commiter_config(id, name, description, commiter_id, fs_directory) VALUES (1, 'DefaultConfiguration', 'FileSystemCommitter based default configuration', 1, './demo');

INSERT INTO system_configuration(id, config_key, config_value) VALUES (2, 'SOCIAL_PROJECT_DIR', 'c:/test-social');
INSERT INTO system_configuration(id, config_key, config_value) VALUES (3, 'NORCONEX_BASE_DIR', '/home/flavius/work/sponge-work/norconnex');
INSERT INTO system_configuration(id, config_key, config_value) VALUES (5, 'SOCIAL_URL', 'http://localhost:5050/social');
INSERT INTO system_configuration(id, config_key, config_value) VALUES (7, 'SOCIAL_POLLING_PACE', '1000');
INSERT INTO system_configuration(id, config_key, config_value) VALUES (8, 'RUN_COLLECTOR_LOCAL', 'true');
INSERT INTO system_configuration(id, config_key, config_value) VALUES (9, 'USES_PROXY', 'false');
INSERT INTO system_configuration(id, config_key, config_value) VALUES (10, 'PROXY_HOST', '172.17.150.16');
INSERT INTO system_configuration(id, config_key, config_value) VALUES (11, 'PROXY_PORT', '3128');
INSERT INTO system_configuration(id, config_key, config_value) VALUES (12, 'PROXY_USES_AUTH', 'false');
INSERT INTO system_configuration(id, config_key, config_value) VALUES (13, 'PROXY_USERNAME', '');
INSERT INTO system_configuration(id, config_key, config_value) VALUES (14, 'PROXY_PASSWORD', '');
INSERT INTO system_configuration(id, config_key, config_value) VALUES (15, 'SOCIAL_APP_ID', '');
INSERT INTO system_configuration(id, config_key, config_value) VALUES (16, 'SOCIAL_APP_SECRET', '');
INSERT INTO system_configuration(id, config_key, config_value) VALUES (17, 'NORCONEX_USER_AGENT', 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/604.1 (KHTML, like Gecko) Ubuntu Safari/604.1');
INSERT INTO system_configuration(id, config_key, config_value) VALUES (18, 'BROWSER_AGENTS', 'localhost:6789');

COMMIT;
