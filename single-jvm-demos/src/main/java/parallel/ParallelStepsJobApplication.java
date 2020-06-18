package parallel;


import domain.Transaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;

@EnableBatchProcessing
@SpringBootApplication
public class ParallelStepsJobApplication {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    public static void main(String[] args) {
        String [] newArgs = new String[] {"inputFlatFile=/data/csv/bigtransactions.csv",
                "inputXmlFile=/data/xml/bigtransactions.xml"};
        SpringApplication.run(ParallelStepsJobApplication.class, newArgs);
    }

    /**
     *
     * Parallel Steps Job
     * @return
     */
    @Bean
    public Job parallelStepsJob() {

        Flow secondFlow = new FlowBuilder<Flow>("secondFlow")
                .start(this.step2())
                .build();
        // start with step1 - split flow add second flow and run in parallel
        Flow parallelFlow = new FlowBuilder<Flow>("parallelFlow")
                .start(this.step1())
                .split(new SimpleAsyncTaskExecutor())
                .add(secondFlow)
                .build();

        return this.jobBuilderFactory.get("parallelStepsJob")
                .start(parallelFlow)
                .end()
                .build();
    }


    /**
     * File Transaction Reader
     * Reads from csv file
     * @param resource
     * @return
     */
    @Bean
    @StepScope
    public FlatFileItemReader<Transaction> fileTransactionReader(
            @Value("#{jobParameters['inputFlatFile']}") Resource resource) {

        return new FlatFileItemReaderBuilder<Transaction>()
                .name("flatFileTransactionReader")
                .resource(resource)
                .delimited()
                .names(new String[] {"account", "amount", "timestamp"})
                .fieldSetMapper(fieldSet -> {
                    Transaction transaction = new Transaction();
                    transaction.setAccount(fieldSet.readString("account"));
                    transaction.setAmount(fieldSet.readBigDecimal("amount"));
                    transaction.setTimestamp(fieldSet.readDate("timestamp", "yyyy-MM-dd HH:mm:ss"));
                    return transaction;
                })
                .build();
    }

    /**
     * XML Transaction Reader
     * @param resource
     * @return
     */
    @Bean
    @StepScope
    public StaxEventItemReader<Transaction> xmlTransactionReader(
            @Value("#{jobParameters['inputXmlFile']}") Resource resource) {

        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(Transaction.class);

        return new StaxEventItemReaderBuilder<Transaction>()
                .name("xmlFileTransactionReader")
                .resource(resource)
                .addFragmentRootElements("transaction")
                .unmarshaller(unmarshaller)
                .build();
    }

    /**
     * Writer : write to database
     * @param dataSource
     * @return
     */
    @Bean
    @StepScope
    public JdbcBatchItemWriter<Transaction> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .dataSource(dataSource)
                .beanMapped()
                .sql("INSERT INTO TRANSACTION (ACCOUNT, AMOUNT, TIMESTAMP) VALUES (:account, :amount, :timestamp)")
                .build();
    }

    /**
     * Step1 -> XML Transaction Reader
     * @return
     */
    @Bean
    public Step step1() {
        return this.stepBuilderFactory.get("step1")
                .<Transaction, Transaction>chunk(100)
                .reader(this.xmlTransactionReader(null))
                .writer(this.writer(null))
                .build();
    }

    /**
     * Step2 -> CSV File Transaction Reader
     * @return
     */
    @Bean
    public Step step2() {
        return this.stepBuilderFactory.get("step2")
                .<Transaction, Transaction>chunk(100)
                .reader(this.fileTransactionReader(null))
                .writer(writer(null))
                .build();
    }

//	@Bean
//	public Job sequentialStepsJob() {
//		return this.jobBuilderFactory.get("sequentialStepsJob")
//				.start(step1())
//				.next(step2())
//				.build();
//	}

}
