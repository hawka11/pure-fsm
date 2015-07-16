package pure.fsm.dropwizard.mysql.bundle;

import io.dropwizard.db.DataSourceFactory;

public interface PureFsmMysqlConfig {

    DataSourceFactory getPureFsmDatabase();
}
