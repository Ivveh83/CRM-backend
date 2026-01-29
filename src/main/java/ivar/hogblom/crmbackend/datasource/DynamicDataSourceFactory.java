package ivar.hogblom.crmbackend.datasource;

import ivar.hogblom.crmbackend.system.entity.db.DatabaseConnection;

import javax.sql.DataSource;

public interface DynamicDataSourceFactory {
    boolean supports(String type);
    DataSource create(DatabaseConnection conn);
}
