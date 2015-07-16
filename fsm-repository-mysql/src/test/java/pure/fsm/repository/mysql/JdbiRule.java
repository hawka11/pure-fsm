package pure.fsm.repository.mysql;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import org.junit.rules.ExternalResource;
import org.skife.jdbi.v2.DBI;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class JdbiRule extends ExternalResource {

    public DBI DBI;
    public ManagedDataSource DATA_SOURCE;

    private final String jdbiConfigFile;

    public JdbiRule(String jdbiConfigFile) {
        this.jdbiConfigFile = jdbiConfigFile;
    }

    @Override
    protected void before() throws Throwable {
        final DataSourceFactory dataSourceFactory = new TestConfigurationLoader().load(new ObjectMapper(), jdbiConfigFile);
        DATA_SOURCE = dataSourceFactory.build(new MetricRegistry(), "");
        DBI = new DBI(DATA_SOURCE);
    }

    private static class TestConfigurationLoader {

        public static final ConfigProvider CONFIG_PROVIDER = new ConfigProvider();

        public DataSourceFactory load(ObjectMapper mapper, String uri) {
            try {
                final ConfigurationFactory<DataSourceFactory> factory = new ConfigurationFactory<>(
                        DataSourceFactory.class,
                        new AlwaysValidValidator(),
                        mapper,
                        "jdbi-test-rule");

                return factory.build(CONFIG_PROVIDER, uri);
            } catch (Exception e) {
                throw new IllegalArgumentException("error parsing jdbi configuration file [" + uri + "]", e);
            }
        }
    }

    private static class ConfigProvider implements ConfigurationSourceProvider {
        @Override
        public InputStream open(String path) throws IOException {
            return getClass().getClassLoader().getResourceAsStream(path);
        }
    }


    private static class AlwaysValidValidator implements Validator {
        @Override
        public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
            return newHashSet();
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
            return newHashSet();
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
            return newHashSet();
        }

        @Override
        public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
            return null;
        }

        @Override
        public <T> T unwrap(Class<T> type) {
            return null;
        }

        @Override
        public ExecutableValidator forExecutables() {
            return null;
        }
    }
}
