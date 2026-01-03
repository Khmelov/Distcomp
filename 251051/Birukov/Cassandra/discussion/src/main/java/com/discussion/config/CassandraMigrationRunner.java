package com.discussion.config;

import com.datastax.oss.driver.api.core.CqlSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CassandraMigrationRunner implements CommandLineRunner {
	
	private final CqlSession cqlSession;
	
	private final String changeLogPath;

	public CassandraMigrationRunner(CqlSession cqlSession,
									@Value("${spring.liquibase.change-log:classpath:/db/changelog/db.changelog-master.yaml}")
									String changeLogPath) {
		this.cqlSession = cqlSession;
		this.changeLogPath = changeLogPath;
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			createKeyspace();
			
			createMigrationTable();
			
			executeMigrations();
			
		} catch (Exception e) {
			throw e;
		}
	}

	 private void createKeyspace() {
		String createKeyspace =
			"CREATE KEYSPACE IF NOT EXISTS distcomp " +
			"WITH replication = {" +
			"	 'class': 'SimpleStrategy'," +
			"	 'replication_factor': 1" +
			"}";
		
		try {
			cqlSession.execute(createKeyspace);
		} catch (Exception e) {
			throw e;
		}
	 }

	 private void createMigrationTable() {
		String createTable =
			"CREATE TABLE IF NOT EXISTS distcomp.schema_migrations (" +
			"	 migration_id TEXT PRIMARY KEY," +
			"	 applied_at TIMESTAMP," +
			"	 checksum TEXT," +
			"	 description TEXT" +
			")";
		
		try {
			cqlSession.execute(createTable);
		} catch (Exception e) {
			throw e;
		}
	 }

	 private void executeMigrations() throws Exception {
		ClassPathResource masterChangelog = new ClassPathResource(changeLogPath);
		
		if (!masterChangelog.exists()) {
			executeDefaultMigrations();
			return;
		}
		
		Yaml yaml = new Yaml();
		try (InputStream inputStream = masterChangelog.getInputStream()) {
			Map<String, Object> changelog = yaml.load(inputStream);
			
			if (changelog != null && changelog.containsKey("databaseChangeLog")) {
				@SuppressWarnings("unchecked")
				Map<String, Object> dbChangeLog = (Map<String, Object>) changelog.get("databaseChangeLog");
					
				if (dbChangeLog.containsKey("include")) {
					@SuppressWarnings("unchecked")
					List<Map<String, Object>> includes = (List<Map<String, Object>>) dbChangeLog.get("include");
					
					for (Map<String, Object> include : includes) {
						String file = (String) include.get("file");
						if (file != null) {
							executeChangelogFile(file);
						}
					}
				}
			}
		}
	 }

	private void executeChangelogFile(String changelogFile) throws Exception {
		ClassPathResource resource = new ClassPathResource("db/changelog/" + changelogFile);
		
		if (resource.exists()) {
			Yaml yaml = new Yaml();
			try (InputStream inputStream = resource.getInputStream()) {
				Map<String, Object> changelog = yaml.load(inputStream);
					
				if (changelog != null && changelog.containsKey("databaseChangeLog")) {
					@SuppressWarnings("unchecked")
					Map<String, Object> dbChangeLog = (Map<String, Object>) changelog.get("databaseChangeLog");
					
					if (dbChangeLog.containsKey("changeSet")) {
						@SuppressWarnings("unchecked")
						List<Map<String, Object>> changesets = (List<Map<String, Object>>) dbChangeLog.get("changeSet");
						
						for (Map<String, Object> changeset : changesets) {
							String id = (String) changeset.get("id");
							String author = (String) changeset.get("author");
							
							if (changeset.containsKey("sqlFile")) {
								@SuppressWarnings("unchecked")
								Map<String, Object> sqlFile = (Map<String, Object>) changeset.get("sqlFile");
								String path = (String) sqlFile.get("path");
								executeSqlFile(path, id, author);
							}
						}
					}
				}
			}
		}
		
	}

	 private void executeSqlFile(String sqlFile, String changesetId, String author) throws Exception {
		ClassPathResource resource = new ClassPathResource("db/changelog/" + sqlFile);
		
		if (resource.exists()) {
			String sql = new String(resource.getInputStream().readAllBytes());
			String[] statements = sql.split(";\\s*");
			
			for (String statement : statements) {
				String trimmedStatement = statement.trim();
				if (!trimmedStatement.isEmpty() && !trimmedStatement.startsWith("--")) {
					
					try {
						cqlSession.execute(trimmedStatement);
						
						recordMigration(changesetId, author, sqlFile, trimmedStatement);
					} catch (Exception e) {
						throw e;
					}
				}
			}
		}
	 }

	 private void recordMigration(String id, String author, String filename, String sql) {
		String migrationId = id + "_" + author;
		String description = "Executed: " + sql.substring(0, Math.min(100, sql.length()));
		String checksum = Integer.toHexString((id + author + sql).hashCode());
		
		String insertMigration =
			"INSERT INTO distcomp.schema_migrations " +
			"(migration_id, applied_at, checksum, description) " +
			"VALUES (?, toTimestamp(now()), ?, ?)";
		
		try {
			cqlSession.execute(insertMigration, migrationId, checksum, description);
		} catch (Exception e) {
			throw e;
		}
	 }

	 private void executeDefaultMigrations() {
		String createNoteTable =
			"CREATE TABLE IF NOT EXISTS distcomp.tbl_note (" +
			"	 country TEXT," +
			"	 id BIGINT," +
			"	 content TEXT," +
			"	 tweet_id BIGINT," +
			"	 PRIMARY KEY ((country), id)" +
			") WITH CLUSTERING ORDER BY (id DESC)";
		
		String createIndex =
			"CREATE INDEX IF NOT EXISTS ON distcomp.tbl_note (tweet_id)";
		
		// Миграция 3: Вставка тестовых данных
		String insertData =
			"INSERT INTO distcomp.tbl_note (country, id, content, tweet_id) " +
			"VALUES ('US', 1, 'Hello from USA', 1001)";
		
		try {
			cqlSession.execute(createNoteTable);
			recordMigration("001", "system", "create_table_note", createNoteTable);
			
			cqlSession.execute(createIndex);
			recordMigration("002", "system", "create_index", createIndex);
			
			cqlSession.execute(insertData);
			recordMigration("003", "system", "insert_test_data", insertData);
		} catch (Exception e) {
			throw e;
		}
	 }
}