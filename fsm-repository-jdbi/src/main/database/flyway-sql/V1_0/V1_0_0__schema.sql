USE purefsm;

DROP TABLE IF EXISTS statemachineid;
DROP TABLE IF EXISTS statemachine;
DROP TABLE IF EXISTS statemachine_lock;


CREATE TABLE IF NOT EXISTS next_statemachine_id (
  id BIGINT UNSIGNED NOT NULL DEFAULT 1         COMMENT 'The state machine id counter'
)
  ENGINE = INNODB
  DEFAULT CHARSET = utf8
  COMMENT 'Next State Machine Id';


CREATE TABLE IF NOT EXISTS statemachine (
  id             BIGINT UNSIGNED NOT NULL        COMMENT 'Unique State Machine ID',
  data           TEXT   DEFAULT NULL             COMMENT 'Serialized state machine transition state',

  CONSTRAINT statemachine_pk
  PRIMARY KEY (id)
)
  ENGINE = INNODB
  DEFAULT CHARSET = utf8
  COMMENT 'State Machines';


CREATE TABLE IF NOT EXISTS statemachine_lock (
  id             SERIAL                                      COMMENT 'ID',

  CONSTRAINT statemachine_lock_pk
  PRIMARY KEY (id),

  CONSTRAINT statemachine_fk
  FOREIGN KEY (id) REFERENCES statemachine(id)
)
  ENGINE = INNODB
  DEFAULT CHARSET = utf8
  COMMENT 'State Machine Locks';

