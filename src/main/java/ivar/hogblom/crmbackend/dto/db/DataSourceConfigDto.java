package ivar.hogblom.crmbackend.dto.db;

import lombok.Data;

@Data
public class DataSourceConfigDto {
    private String type;          // "postgres", "mysql", "mariadb", "sqlite", "sqlcipher"
    private String host;          // ej för sqlite
    private Integer port;         // ej för sqlite
    private String database;      // ej för sqlite (används av postgres/mysql)
    private String username;      // ej för sqlite
    private String password;      // ej för sqlite
    private String filePath;      // sqlite / sqlcipher
    private String encryptionKey; // sqlcipher (om du vill)
}
