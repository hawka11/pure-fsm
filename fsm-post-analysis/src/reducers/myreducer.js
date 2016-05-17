
const initialState = {};

import { LOAD_FSM } from '../actions/const';

module.exports = function(state = initialState, action) {

  switch(action.type) {

    case LOAD_FSM: {

      let transition = {
        transitioned: '20170517050000',
        state: "RechargeRequested",
        event: "RequestRecharge",
        previous: {
              transitioned: '20170517040000',
              state: "InitialState",
              event: null,
              previous: null,
              context: {
                data: [{
                  id: 1000
                }]
              }
        },
        context: {
          data: [{
            id: 1000
          }, {
            pins: ['5453399225', '6655343']
          }]
        }
      };

      debugger;

      let nextState = Object.assign({transition: transition}, state);
      return nextState;
    } break;
    default: {
      return state;
    }
  }
}
