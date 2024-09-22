package org.cranes.team.outboxstarter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

@Service
public class JdbcPartitionCleanerService {

    private static final Logger logger = LoggerFactory.getLogger(JdbcPartitionCleanerService.class);

    @Value("${outbox.cleaner.retention-period-in-months}")
    private int retentionPeriodInMonths;

    @Value("${outbox.cleaner.batch-size}")
    private int batchSize;

    private final DataSource dataSource;

    public JdbcPartitionCleanerService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cleanPartitions() {
        try (Connection connection = dataSource.getConnection()) {
            LocalDate cutoffDate = LocalDate.now().minusMonths(retentionPeriodInMonths);

            // Удаляем устаревшие записи из каждой партиции
            for (int month = 1; month <= 12; month++) {
                deleteRecords(connection, month, cutoffDate);
            }
        } catch (SQLException e) {
            logger.error("Error cleaning partitions", e);
        }
    }

    private void deleteRecords(Connection connection, int month, LocalDate cutoffDate) {
        String partitionName = String.format("outbox_m%d", month);
        String deleteQuery = String.format("DELETE FROM %s WHERE created_at < ? LIMIT ?", partitionName);

        try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.setTimestamp(1, Timestamp.valueOf(cutoffDate.atStartOfDay()));
            stmt.setInt(2, batchSize);
            int deletedRows = stmt.executeUpdate();
            logger.info("Deleted {} records from partition {}", deletedRows, partitionName);
        } catch (SQLException e) {
            logger.error("Error deleting records from partition {}", partitionName, e);
        }
    }
}
