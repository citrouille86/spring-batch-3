package fr.hm.demo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import fr.hm.demo.dto.Marksheet;

@Component
public class StepCompletionNotificationListener extends StepExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(StepCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StepCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if(stepExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! STEP FINISHED !!! Time to verify the results");

            jdbcTemplate.query("SELECT stdId, totalSubMark FROM marksheet",
                (rs, row) -> new Marksheet(
                    rs.getString(1),
                    rs.getInt(2))
            ).forEach(mark -> log.info("Found <" + mark + "> in the database."));
        }
        return super.afterStep(stepExecution);
    }
}