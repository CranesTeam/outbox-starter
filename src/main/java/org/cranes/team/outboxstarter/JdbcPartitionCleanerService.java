package org.cranes.team.outboxstarter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

@Service
public class JdbcPartitionCleanerService {

    private static final Logger logger = LoggerFactory.getLogger(JdbcPartitionCleanerService.class);

    @Value("${outbox.cleaner.batch-size}")
    private int batchSize;
    private final DataSource dataSource;

    private final String QUERY = "DELETE FROM %s WHERE created_at < NOW() LIMIT ?";

    public JdbcPartitionCleanerService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cleanPartitions() {
        try (Connection connection = dataSource.getConnection()) {
            LocalDate currentDate = LocalDate.now();
            int currentYear = currentDate.getYear();
            int currentQuarter = (currentDate.getMonthValue() - 1) / 3 + 1;

            for (int year = currentYear - 1; year <= currentYear; year++) {
                for (int quarter = 1; quarter <= 4; quarter++) {
                    if (year == currentYear && quarter >= currentQuarter) {
                        break;
                    }

                    String partitionName = String.format("outbox_%d_q%d", year, quarter);
                    int deletedRows = deleteRecords(connection, partitionName);
                    if (deletedRows < batchSize) {
                        return;
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("Error cleaning partitions", e);
        }

    }

    private int deleteRecords(Connection connection, String partitionName) {
        String deleteQuery = String.format(QUERY, partitionName);

        try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.setInt(1, batchSize);
            int deletedRows = stmt.executeUpdate();

            logger.info("Deleted {} records from partition {}", deletedRows, partitionName);
            return deletedRows;
        } catch (SQLException e) {
            logger.error("Error deleting records from partition {}", partitionName, e);
            return 0;
        }
    }
}