package com.discussion.config;

import com.datastax.oss.driver.api.core.CqlSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

@Slf4j
@Configuration
public class LiquibaseConfig {
	
    public static class CassandraDataSourceWrapper implements DataSource {
        
        private PrintWriter logWriter;
        
        @Override
        public Connection getConnection() throws SQLException {
            throw new SQLFeatureNotSupportedException("Cassandra does not support JDBC connections. Use CQL directly.");
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            throw new SQLFeatureNotSupportedException("Cassandra does not support JDBC connections. Use CQL directly.");
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return logWriter;
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            this.logWriter = out;
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {}

        @Override
        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new SQLFeatureNotSupportedException("getParentLogger not supported");
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            if (iface.isInstance(this)) {
                return iface.cast(this);
            }
            throw new SQLException("DataSource of type [" + getClass().getName() +
                    "] cannot be unwrapped as [" + iface.getName() + "]");
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return iface.isInstance(this);
        }
    }
}