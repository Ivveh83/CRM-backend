package ivar.hogblom.crmbackend.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ivar.hogblom.crmbackend.system.entity.db.DatabaseConnection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
@AllArgsConstructor
public class SQLiteDataSourceFactory implements DynamicDataSourceFactory {

    @Override
    public boolean supports(String type) {
        return "sqlite".equalsIgnoreCase(type);
    }

        @Override
        public DataSource create(DatabaseConnection c) {

            HikariConfig h = new HikariConfig();
            h.setDriverClassName("org.sqlite.JDBC");
            h.setJdbcUrl("jdbc:sqlite:" + c.getFilePath());
            h.setMaximumPoolSize(5);

            HikariDataSource ds = new HikariDataSource(h);

            try (Connection conn = ds.getConnection();
                 Statement st = conn.createStatement()) {

                // Basic connectivity test
                st.execute("SELECT 1");


            } catch (Exception e) {
                ds.close();
                throw new IllegalStateException("Failed to open SQLite database", e);
            }

            return ds;
        }


    }

//      Premium-versionen med SQLCipher nedan, injicera CryptoService i constructor.
//    @Override
//    public DataSource create(DatabaseConnection c) {
//
//        //Kollar om den ska vara krypterad
//        boolean encrypted =
//                c.getEncryptionKey() != null && !c.getEncryptionKey().isBlank();
//
//        //Skapar och ställer in HikariConfig
//        HikariConfig h = new HikariConfig();
//            h.setDriverClassName("org.sqlite.JDBC");
//            h.setJdbcUrl("jdbc:sqlite:" + c.getFilePath());
//            h.setMaximumPoolSize(5);
//
//            //Skapar en HikariDatasource utifrån de tidigare inställningarna
//        HikariDataSource ds = new HikariDataSource(h);
//
//        // ✅ VERIFIERA
//        //Initierar en nyckel som är null, om encrypte är truthy nedan så dekrypteras den nyckel
//        // som finns i DatabaseConnection, annars används inte plainkey nedan.
//        String plainKey = null;
//        if (encrypted) {
//            plainKey = cryptoService.decrypt(c.getEncryptionKey());
//            if (plainKey == null || plainKey.isBlank()) {
//                throw new IllegalStateException("Decrypted encryption key is empty");
//            }
//        }
//
//        try (Connection conn = ds.getConnection();
//             Statement st = conn.createStatement()) {
//
//
//            //Validering
//            if (encrypted) {
//                //Garanterar korrekt SQL
//                String key = plainKey.replace("'", "''");
//                //Sätter krypteringsnyckeln för den aktuella connectionen
//                st.execute("PRAGMA key = '" + key + "';");
//                //Talar om för SQLCipher att databasen ska tolkas som version 4-format
//                st.execute("PRAGMA cipher_compatibility = 4;");
//
//            }
//
//            // Verifierar / tvingar dekryptering
//            /*
//            * ✅ Nyckeln är rätt → allt funkar
//            * ❌ Nyckeln är fel → exception kastas
//            * ❌ Databasen är trasig → exception kastas
//            */
//            st.execute("SELECT count(*) FROM sqlite_master");
//
//        } catch (Exception e) {
//            ds.close();
//            throw new IllegalStateException(
//                    encrypted
//                            ? "Invalid encryption key or corrupted encrypted database"
//                            : "Failed to open SQLite database",
//                    e
//            );
//        }
//
//        return ds;
//    }
//// Optional helper for SQLCipher escape:
//private String escapeSql(String input) {
//    return input == null ? null : input.replace("'", "''");
//}