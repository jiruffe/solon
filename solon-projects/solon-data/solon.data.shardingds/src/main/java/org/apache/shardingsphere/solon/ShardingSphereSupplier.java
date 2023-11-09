package org.apache.shardingsphere.solon;

import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.noear.solon.Utils;
import org.noear.solon.core.util.ResourceUtil;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * @author noear
 * @since 2.2
 */
public class ShardingSphereSupplier implements Supplier<DataSource> {

    private final Properties properties;

    public ShardingSphereSupplier(Properties properties) {
        this.properties = properties;
    }

    @Override
    public DataSource get() {
        try {
            String fileUri = properties.getProperty("file");
            if (Utils.isNotEmpty(fileUri)) {
                String fileContent = ResourceUtil.findResourceAsString(fileUri);
                if (fileContent == null){
                    throw new IllegalStateException("The sharding sphere configuration file does not exist");
                }
                return YamlShardingSphereDataSourceFactory.createDataSource(fileContent.getBytes());
            }

            String configTxt = properties.getProperty("config");
            if (Utils.isNotEmpty(configTxt)) {
                return YamlShardingSphereDataSourceFactory.createDataSource(configTxt.getBytes());
            }
        } catch (Exception e) {
            throw new IllegalStateException("The sharding sphere configuration failed", e);
        }

        throw new IllegalStateException("Invalid sharding sphere configuration");
    }
}
