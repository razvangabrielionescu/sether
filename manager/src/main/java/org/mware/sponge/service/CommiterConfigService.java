package org.mware.sponge.service;

import org.mware.sponge.domain.CommiterConfig;
import org.mware.sponge.domain.CommiterFieldMapping;
import org.mware.sponge.repository.CommiterConfigRepository;
import org.mware.sponge.repository.CommiterFieldMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

/**
 * Created by Dan on 9/30/2017.
 */
@Service
@Transactional
public class CommiterConfigService {
    private final Logger log = LoggerFactory.getLogger(CommiterConfigService.class);

    private final CommiterConfigRepository commiterConfigRepository;
    private final CommiterFieldMappingRepository commiterFieldMappingRepository;

    /**
     * @param commiterConfigRepository
     * @param commiterFieldMappingRepository
     */
    public CommiterConfigService(CommiterConfigRepository commiterConfigRepository,
                                    CommiterFieldMappingRepository commiterFieldMappingRepository) {
        this.commiterConfigRepository = commiterConfigRepository;
        this.commiterFieldMappingRepository = commiterFieldMappingRepository;
    }

    /**
     * @param id
     * @param name
     * @param withMapping
     * @return
     */
    public CommiterConfig cloneCommiterConfig(Long id, String name, boolean withMapping) {
        CommiterConfig oldCommiterConfig = commiterConfigRepository.findOne(id);
        CommiterConfig newCommiterConfig = new CommiterConfig();

        this.copyFields(newCommiterConfig, oldCommiterConfig);
        newCommiterConfig.setName(name);
        CommiterConfig commiterConfig = commiterConfigRepository.save(newCommiterConfig);

        if (withMapping) {
            this.copyFieldMapping(commiterConfig, oldCommiterConfig);
        }

        return commiterConfig;
    }

    private void copyFields(CommiterConfig newCommiterConfig, CommiterConfig oldCommiterConfig) {
        newCommiterConfig.setCommiter(oldCommiterConfig.getCommiter());
        newCommiterConfig.setBcPassword(oldCommiterConfig.getBcPassword());
        newCommiterConfig.setBcUrl(oldCommiterConfig.getBcUrl());
        newCommiterConfig.setBcUsername(oldCommiterConfig.getBcUsername());
        newCommiterConfig.setDbCommitBatchSize(oldCommiterConfig.getDbCommitBatchSize());
        newCommiterConfig.setDbConnectionUrl(oldCommiterConfig.getDbConnectionUrl());
        newCommiterConfig.setDbCreateMissing(oldCommiterConfig.getDbCreateMissing());
        newCommiterConfig.setDbCreateTableSQL(oldCommiterConfig.getDbCreateTableSQL());
        newCommiterConfig.setDbDriverClass(oldCommiterConfig.getDbDriverClass());
        newCommiterConfig.setDbDriverPath(oldCommiterConfig.getDbDriverPath());
        newCommiterConfig.setDbPassword(oldCommiterConfig.getDbPassword());
        newCommiterConfig.setDbUsername(oldCommiterConfig.getDbUsername());
        newCommiterConfig.setDescription(oldCommiterConfig.getDescription());
        newCommiterConfig.setFsDirectory(oldCommiterConfig.getFsDirectory());
    }

    private void copyFieldMapping(CommiterConfig newCommiterConfig, CommiterConfig oldCommiterConfig) {
        newCommiterConfig.setCommiterFieldMappings(new HashSet<CommiterFieldMapping>());
        CommiterFieldMapping fieldMapping = null;
        for (CommiterFieldMapping commiterFieldMapping : oldCommiterConfig.getCommiterFieldMappings()) {
            fieldMapping =  new CommiterFieldMapping(commiterFieldMapping.getSourceField(),
                                                     commiterFieldMapping.getDestinationField(),
                                                     newCommiterConfig);
            newCommiterConfig.getCommiterFieldMappings().add(fieldMapping);
            commiterFieldMappingRepository.save(fieldMapping);
        }
    }
}
