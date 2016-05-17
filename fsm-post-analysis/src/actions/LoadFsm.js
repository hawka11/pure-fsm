import {LOAD_FSM} from './const';

module.exports = function(parameter) {
  return {
      type: LOAD_FSM,
      parameter
    };
};
