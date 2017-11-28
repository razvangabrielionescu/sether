package org.mware.sponge.util;

import java.io.File;

/**
 * Created by Dan on 7/11/2017.
 */
public class Constants {
//    System Config keys
    public static final String CONFIG_KEY_SOCIAL_PROJECT_DIR = "SOCIAL_PROJECT_DIR";
    public static final String CONFIG_KEY_NORCONNEX_BASE_DIR = "NORCONEX_BASE_DIR";
    public static final String CONFIG_KEY_SOCIAL_URL = "SOCIAL_URL";
    public static final String CONFIG_KEY_SOCIAL_POLLING_PACE = "SOCIAL_POLLING_PACE";
    public static final String CONFIG_KEY_RUN_COLLECTOR_LOCAL = "RUN_COLLECTOR_LOCAL";
    public static final String CONFIG_KEY_USES_PROXY = "USES_PROXY";
    public static final String CONFIG_KEY_PROXY_HOST = "PROXY_HOST";
    public static final String CONFIG_KEY_PROXY_PORT = "PROXY_PORT";
    public static final String CONFIG_KEY_PROXY_USES_AUTH = "PROXY_USES_AUTH";
    public static final String CONFIG_KEY_PROXY_USERNAME = "PROXY_USERNAME";
    public static final String CONFIG_KEY_PROXY_PASSWORD = "PROXY_PASSWORD";
    public static final String CONFIG_KEY_SOCIAL_APP_ID = "SOCIAL_APP_ID";
    public static final String CONFIG_KEY_SOCIAL_APP_SECRET = "SOCIAL_APP_SECRET";
    public static final String CONFIG_KEY_NORCONEX_USER_AGENT = "NORCONEX_USER_AGENT";
    public static final String CONFIG_KEY_BROWSER_AGENTS = "BROWSER_AGENTS";

//    Norconnex
    public static final String NORCONNEX_INPUT_SUFFIX = "input";
    public static final String NORCONNEX_LOGS_SUFFIX = "output" + File.separator + "logs";
    public static final String NORCONNEX_PROGRESS_SUFFIX = "output" + File.separator + "progress";
    public static final String NORCONNEX_PROGRESS_LATEST_SUFFIX = NORCONNEX_PROGRESS_SUFFIX + File.separator + "latest";
    public static final String NORCONNEX_COMPLETED_STATUS = "COMPLETED";
//    Defaults
    public static final int NORCONNEX_CRAWLER_DEFAULT_MAX_DEPTH = 10;
    public static final int NORCONNEX_CRAWLER_DEFAULT_THREAD_NUM = 2;
    public static final String NORCONNEX_CRAWLER_DEFAULT_EXTENSIONS_REJECT = "jpg,gif,png,ico,css,js";
    public static final boolean NORCONNEX_CRAWL_SCOPE_STRATEGY_STAY_ON_DOMAIN = true;
    public static final boolean NORCONNEX_CRAWL_SCOPE_STRATEGY_STAY_ON_PORT = false;
    public static final boolean NORCONNEX_CRAWL_SCOPE_STRATEGY_STAY_ON_PROTOCOL = false;
    //TODO - The below are not provided by Portia - need a method to configure them
    public static final String NORCONNEX_DEFAULT_AUTH_METHOD = "form";
    public static final String NORCONNEX_DEFAULT_AUTH_FORM_USERNAME_FIELD = "username";
    public static final String NORCONNEX_DEFAULT_AUTH_FORM_PASSWORD_FIELD = "password";
    public static final String NORCONNEX_DEFAULT_FILE_SYSTEM_CRAWLER_ID = "File System Crawler";

//    Portia
    public static final String PORTIA_PROJECT_SCHEMA_FILE_NAME = "items.json";
    public static final String PORTIA_PROJECT_SPIDERS_FOLDER = "spiders";

//    Tool names
    public static final String TOOL_WEBUI = "Web UI";
    public static final String TOOL_SOCIAL = "Social";
    public static final String TOOL_FILESYSTEM = "FileSystem";

//    Misc
    public static final String COMMAND_START_PREFIX = "START";
    public static final String COMMAND_STOP_PREFIX = "STOP";
    public static final int SCHEDULER_POOL_SIZE = 2;
    public static final int BROWSER_CALLBACK_SERVER_PORT = 6790;

//    Committers
    public static final String COMMITTER_FILESYSTEM_NAME = "FileSystemCommitter";
    public static final String COMMITTER_JSON_NAME = "JSONFileCommitter";
    public static final String COMMITTER_XML_NAME = "XMLFileCommitter";
    public static final String COMMITTER_DATABASE_NAME = "SQLCommitter";
    public static final String COMMITTER_BIGCONNECT_NAME = "BigConnectCommitter";
}
