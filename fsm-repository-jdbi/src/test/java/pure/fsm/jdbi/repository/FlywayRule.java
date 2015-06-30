package pure.fsm.jdbi.repository;

import org.flywaydb.core.Flyway;
import org.glassfish.jersey.internal.util.Producer;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static java.lang.String.format;
import static org.apache.commons.io.FileUtils.openInputStream;
import static org.apache.commons.io.IOUtils.closeQuietly;

public class FlywayRule extends ExternalResource {

    private static final Logger LOG = LoggerFactory.getLogger(FlywayRule.class);

    private final String propertiesRelPath;
    private final String migrationRelPath;
    private final Producer<DataSource> dataSource;

    public FlywayRule(String propertiesRelPath, String migrationRelPath, Producer<DataSource> dataSource) {
        this.propertiesRelPath = propertiesRelPath;
        this.migrationRelPath = migrationRelPath;
        this.dataSource = dataSource;
    }

    @Override
    protected void before() throws Throwable {
        Flyway flyway = new Flyway();

        flyway.configure(loadProperties(propertiesRelPath));
        flyway.setLocations(flywayLocation(migrationRelPath));
        flyway.setDataSource(dataSource.call());

        flyway.clean(); //destroy schema

        flyway.migrate();
    }

    private Properties loadProperties(String relPath) {
        final String absPath = absResource(relPath);
        final File file = new File(absPath);
        final Properties properties = new Properties();

        FileInputStream fis = null;
        try {
            fis = openInputStream(file);
            properties.load(fis);
        } catch (IOException e) {
            LOG.error(format("could not open file [%s]", absPath), e);
        } finally {
            closeQuietly(fis);
        }

        return properties;
    }

    private String flywayLocation(String relPath) {
        return format("filesystem:%s", absResource(relPath));
    }

    private String absResource(String relPath) {
        @SuppressWarnings("ConstantConditions")
        final String path = getClass().getClassLoader().getResource("").getPath();

        //always running from .../build/classes/test
        final String rootProjectPath = new File(path).getParentFile().getParentFile().getParentFile().getParentFile().getPath();

        return format("%s/%s", rootProjectPath, relPath);
    }
}
