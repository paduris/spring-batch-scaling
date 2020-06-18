

docker run -d -p 3306:3306 --name mysql_server -e MYSQL_ROOT_PASSWORD=p@ssw0rd mysql:latest
docker exec -it mysql_server mysql -uroot -p

**Log**
2020-06-16 20:58:30.661 DEBUG 62016 --- [lTaskExecutor-2] o.s.jdbc.core.JdbcTemplate               : Executing prepared SQL query
2020-06-16 20:58:30.661 DEBUG 62016 --- [lTaskExecutor-2] o.s.jdbc.core.JdbcTemplate               : Executing prepared SQL statement [SELECT VERSION FROM BATCH_JOB_EXECUTION WHERE JOB_EXECUTION_ID=?]
2020-06-16 20:58:30.683 DEBUG 62016 --- [lTaskExecutor-2] o.s.jdbc.core.JdbcTemplate               : Executing SQL batch update [INSERT INTO TRANSACTION (ACCOUNT, AMOUNT, TIMESTAMP) VALUES (?, ?, ?)]
2020-06-16 20:58:30.683 DEBUG 62016 --- [lTaskExecutor-2] o.s.jdbc.core.JdbcTemplate               : Executing prepared SQL statement [INSERT INTO TRANSACTION (ACCOUNT, AMOUNT, TIMESTAMP) VALUES (?, ?, ?)]
2020-06-16 20:58:30.784 DEBUG 62016 --- [lTaskExecutor-3] o.s.jdbc.core.JdbcTemplate               : Executing prepared SQL update
2020-06-16 20:58:30.785 DEBUG 62016 --- [lTaskExecutor-3] o.s.jdbc.core.JdbcTemplate               : Executing prepared SQL statement [UPDATE BATCH_STEP_EXECUTION_CONTEXT SET SHORT_CONTEXT = ?, SERIALIZED_CONTEXT = ? WHERE STEP_EXECUTION_ID = ?]
2020-06-16 20:58:30.794 DEBUG 62016 --- [lTaskExecutor-3] o.s.jdbc.core.JdbcTemplate               : Executing prepared SQL update
2020-06-16 20:58:30.794 DEBUG 62016 --- [lTaskExecutor-3] o.s.jdbc.core.JdbcTemplate               : Executing prepared SQL statement [UPDATE BATCH_STEP_EXECUTION set START_TIME = ?, END_TIME = ?, STATUS = ?, COMMIT_COUNT = ?, READ_COUNT = ?, FILTER_COUNT = ?, WRITE_COUNT = ?, EXIT_CODE = ?, EXIT_MESSAGE = ?, VERSION = ?, READ_SKIP_COUNT = ?, PROCESS_SKIP_COUNT = ?, WRITE_SKIP_COUNT = ?, ROLLBACK_COUNT = ?, LAST_UPDATED = ? where STEP_EXECUTION_ID = ? and VERSION = ?]