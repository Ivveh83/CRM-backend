package ivar.hogblom.crmbackend.dto.db;

import lombok.Data;

@Data
public class DataSourceConfigDto {
    private String type;          // ex. "postgres", "mysql", "mariadb", "sqlite", "sqlcipher", OBS! Idag endast stöd för sqlite
    private String host;          // ej för sqlite
    private Integer port;         // ej för sqlite
    private String database;      // ej för sqlite (används av postgres/mysql)
    private String username;      // ej för sqlite
    private String password;      // ej för sqlite
    private String filePath;      // för sqlite / sqlcipher, alltså absolut filsökväg.
//    private String encryptionKey; // för sqlcipher (valfritt)
}
