package org.mware.sponge.committer;

import com.norconex.committer.core.ICommitter;
import com.norconex.committer.core.impl.FileSystemCommitter;
import com.norconex.committer.core.impl.JSONFileCommitter;
import com.norconex.committer.core.impl.XMLFileCommitter;
import liquibase.util.StringUtils;
import org.mware.sponge.crawl.committer.BigConnectCommitter;
import org.mware.sponge.crawl.committer.SQLCommitter;
import org.mware.sponge.domain.CommiterConfig;
import org.mware.sponge.exception.UnimplementedCommitterException;
import org.mware.sponge.service.dto.ProjectDTO;
import org.mware.sponge.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dan on 7/24/2017.
 */
public class CommitterFactory {
    private static final Logger log = LoggerFactory.getLogger(CommitterFactory.class);

    /**
     * @param project
     * @return
     * @throws UnimplementedCommitterException
     */
    public static ICommitter buildCommitter(ProjectDTO project)
                                                        throws UnimplementedCommitterException {
        String committerName = project.getCommiterConfig().getCommiter().getName();
        log.info("Building committer of type: "+committerName);

        if (committerName.equals(Constants.COMMITTER_FILESYSTEM_NAME)) {
            FileSystemCommitter _committer = new FileSystemCommitter();
            _committer.setDirectory(project.getCommiterConfig().getFsDirectory());

            return _committer;
        } else if (committerName.equals(Constants.COMMITTER_JSON_NAME)) {
            JSONFileCommitter _committer = new JSONFileCommitter();
            _committer.setDirectory(project.getCommiterConfig().getFsDirectory());

            return _committer;
        } else if (committerName.equals(Constants.COMMITTER_XML_NAME)) {
            XMLFileCommitter _committer = new XMLFileCommitter();
            _committer.setDirectory(project.getCommiterConfig().getFsDirectory());

            return _committer;
        } else if (committerName.equals(Constants.COMMITTER_DATABASE_NAME)) {
            SQLCommitter _committer = new SQLCommitter();
            CommiterConfig config = project.getCommiterConfig();
            _committer.setConnectionUrl(config.getDbConnectionUrl());
            if (!StringUtils.isEmpty(config.getDbCreateMissing())) {
                try {
                    _committer.setCreateMissing(Boolean.valueOf(config.getDbCreateMissing()));
                } catch(Exception e) {
                    log.error("Could not set database create missing because value is not boolean: "+e.getMessage());
                    log.info("Setting create missing to default: false");
                    _committer.setCreateMissing(false);
                }
            }
            _committer.setCreateTableSQL(config.getDbCreateTableSQL());
            _committer.setDriverClass(config.getDbDriverClass());
            _committer.setDriverPath(config.getDbDriverPath());
            _committer.setUsername(config.getDbUsername());
            _committer.setPassword(config.getDbPassword());
            try {
                int batchSize = Integer.parseInt(config.getDbCommitBatchSize());
                _committer.setCommitBatchSize(batchSize);
                log.info("Setting queue size (initial 1000) to same size as commit batch size: "+batchSize);
                _committer.setQueueSize(batchSize);
            } catch(Exception e) {
                log.error("Could not set database batch size because value is not number: "+e.getMessage());
                log.error("Setting batch size to default: 1");
                _committer.setCommitBatchSize(1);
            }

            Map<String, String> fieldMapping = new HashMap<String, String>();
            project.getCommiterConfig().getCommiterFieldMappings().forEach((mapping) -> {
                if (!fieldMapping.containsKey(mapping.getSourceField())) {
                    fieldMapping.put(mapping.getSourceField(), mapping.getDestinationField());
                }
            });
            log.info("Adding field mapping of size: "+fieldMapping.size());
            _committer.setFieldMapping(fieldMapping);

            return _committer;
        } if (committerName.equals(Constants.COMMITTER_BIGCONNECT_NAME)) {
            BigConnectCommitter _committer = new BigConnectCommitter();
            CommiterConfig config = project.getCommiterConfig();
            _committer.setUrl(config.getBcUrl());
            _committer.setUsername(config.getBcUsername());
            _committer.setPassword(config.getBcPassword());

            Map<String, String> fieldMapping = new HashMap<String, String>();
            project.getCommiterConfig().getCommiterFieldMappings().forEach((mapping) -> {
                if (!fieldMapping.containsKey(mapping.getSourceField())) {
                    fieldMapping.put(mapping.getSourceField(), mapping.getDestinationField());
                }
            });
            log.info("Adding field mapping of size: "+fieldMapping.size());
            _committer.setFieldMapping(fieldMapping);

            return _committer;
        } else {
            throw new UnimplementedCommitterException("Committer: "+committerName+" is not implemented");
        }
    }
}
