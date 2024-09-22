package org.cranes.team.outboxstarter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import javax.sql.DataSource;

@Configuration
@EnableScheduling
@ConditionalOnBean(DataSource.class)
@ConditionalOnProperty(name = "outbox.cleaner.enable", havingValue = "true", matchIfMissing = false)
public class PartitionCleanerConfig {

    private final DataSource dataSource;

    public PartitionCleanerConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public JdbcPartitionCleanerService partitionCleanerService() {
        return new JdbcPartitionCleanerService(dataSource);
    }

    @Scheduled(cron = "${outbox.cleaner.cron}")
    public void schedulePartitionCleaning() {
        partitionCleanerService().cleanPartitions();
    }

}
