package pure.fsm.telco.user.application;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;

public class HazelcastServerMain {
    public static void main(String[] args) {
        Config config = new Config();
        Hazelcast.newHazelcastInstance(config);
    }
}

