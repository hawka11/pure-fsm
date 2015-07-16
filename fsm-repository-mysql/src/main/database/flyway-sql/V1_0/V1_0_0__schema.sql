USE purefsm;

DROP TABLE IF EXISTS purefsm_statemachine_id;
DROP TABLE IF EXISTS purefsm_statemachine;

/**
 * STATE MACHINE NEXT ID
 */
CREATE TABLE IF NOT EXISTS purefsm_statemachine_id (
  id BIGINT UNSIGNED NOT NULL DEFAULT 1         COMMENT 'The state machine id counter'
)
  ENGINE = INNODB
  DEFAULT CHARSET = utf8
  COMMENT 'Next State Machine Id';

/**
 * STATE MACHINE STATE
 */
CREATE TABLE IF NOT EXISTS purefsm_statemachine (
  id             BIGINT UNSIGNED NOT NULL        COMMENT 'Unique State Machine ID',
  data           TEXT   DEFAULT NULL             COMMENT 'Serialized state machine transition state',

  CONSTRAINT purefsm_statemachine_pk
  PRIMARY KEY (id)
)
  ENGINE = INNODB
  DEFAULT CHARSET = utf8
  COMMENT 'State Machines';

