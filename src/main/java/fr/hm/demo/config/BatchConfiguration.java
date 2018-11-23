package fr.hm.demo.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import fr.hm.demo.dto.Marksheet;
import fr.hm.demo.dto.Student;
import fr.hm.demo.listener.JobCompletionNotificationListener;
import fr.hm.demo.listener.MarksheetWriterListener;
import fr.hm.demo.listener.StepCompletionNotificationListener;
import fr.hm.demo.processor.StudentItemProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Autowired
    public MarksheetWriterListener writerListener;

    @Autowired
    public StepCompletionNotificationListener stepListener;

    @Bean
    public ItemReader<Student> jdbcReader() {
        final JdbcCursorItemReader<Student> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("select * from student where subMarkOne % 2 = 0");
        reader.setRowMapper(
                (resultSet, i) -> new Student(resultSet.getString(1), resultSet.getInt(2), resultSet.getInt(3)));
        return reader;
    }

    @Bean
    public ItemReader<Student> fileReader() {
        return new FlatFileItemReaderBuilder<Student>()
                .resource(new ClassPathResource("data.csv"))
                .lineMapper(new DefaultLineMapper<Student>() {
                    {
                        setLineTokenizer(new DelimitedLineTokenizer() {
                            {
                                setNames("stdId", "subMarkOne", "subMarkTwo");
                            }
                        });
                        setFieldSetMapper(new BeanWrapperFieldSetMapper<Student>() {
                            {
                                setTargetType(Student.class);
                            }
                        });
                    }
                }).build();
    }

    @Bean
    public ItemWriter<Marksheet> jdbcWriter() {
        return new JdbcBatchItemWriterBuilder<Marksheet>()
                .dataSource(dataSource)
                .sql("INSERT INTO marksheet (stdId, totalSubMark) VALUES (:stdId, :totalSubMark)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public ItemWriter<Marksheet> fileWriter() {
        final DelimitedLineAggregator<Marksheet> delLineAgg = new DelimitedLineAggregator<Marksheet>();
        delLineAgg.setDelimiter(",");
        final BeanWrapperFieldExtractor<Marksheet> fieldExtractor = new BeanWrapperFieldExtractor<Marksheet>();
        fieldExtractor.setNames(new String[] { "stdId", "totalSubMark" });
        delLineAgg.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<Marksheet>()
                .resource(new ClassPathResource("output.csv"))
                .lineAggregator(delLineAgg)
                .build();
    }

    @Bean
    public ItemProcessor<Student, Marksheet> processor() {
        return new StudentItemProcessor();
    }

    @Bean
    public Job createMarkSheetJob(JobCompletionNotificationListener jobListener) {
        return jobBuilderFactory
                .get("createMarkSheetJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(stepFileToBdd())
                .next(stepBddToBdd())
                .build();
    }

    @Bean
    public Step stepFileToBdd() {
        return stepBuilderFactory
                .get("stepFileToBdd")
                .listener(stepListener)
                .<Student, Marksheet>chunk(5)
                .reader(fileReader())
                .processor(processor())
                .writer(jdbcWriter())
                .build();
    }

    @Bean
    public Step stepBddToBdd() {
        return stepBuilderFactory
                .get("stepBddToBdd")
                .listener(stepListener)
                .<Student, Marksheet>chunk(5)
                .reader(jdbcReader())
                .processor(processor())
                .writer(jdbcWriter())
                .listener(writerListener)
                .build();
    }
}