package ivar.hogblom.crmbackend.datasource;

// package ivar.hogblom.crmbackend.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> CURRENT_KEY = new ThreadLocal<>();

    private final Map<Object, Object> dynamicTargets = new HashMap<>();


    public static void setCurrentKey(String key) {
        CURRENT_KEY.set(key);
    }

    public static void clear() {
        CURRENT_KEY.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return CURRENT_KEY.get();
    }

    public synchronized void addDataSource(String key, DataSource dataSource) {
        dynamicTargets.put(key, dataSource);
        super.setTargetDataSources(new HashMap<>(dynamicTargets));
        // viktigt för att AbstractRoutingDataSource ska ladda om mappen
        super.afterPropertiesSet();
    }

    public static String getCurrentKey() {
        return CURRENT_KEY.get();
    }

}
