package pure.fsm.dropwizard.mysql.bundle;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class PureFsmMysqlConfig extends Configuration {

    @Valid
    @NotNull
    public final DataSourceFactory database = new DataSourceFactory();
}
