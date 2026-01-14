package com.blog.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.springframework.stereotype.Component;

@Component
public class TablePrefixNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        String tableName = name.getText();

        // Добавляем префикс tbl_ если его еще нет
        if (!tableName.startsWith("tbl_")) {
            tableName = "tbl_" + tableName;
        }

        return Identifier.toIdentifier(tableName, name.isQuoted());
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        String sequenceName = name.getText();

        // Для последовательностей также добавляем префикс
        if (!sequenceName.startsWith("tbl_")) {
            sequenceName = "tbl_" + sequenceName;
        }

        return Identifier.toIdentifier(sequenceName, name.isQuoted());
    }
}