
const initialState = {};

import { LOAD_FSM } from '../actions/const';

module.exports = function(state = initialState, action) {

  switch(action.type) {

    case LOAD_FSM: {

      let transition = {
        transitioned: '20170517050000',
        state: 'RechargeRequested',
        event: 'RequestRecharge',
        previous: {
              transitioned: '20170517040000',
              state: 'InitialState',
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

      let transitions = [];
      while(transition.previous != null) {
        transitions.push(transition);
        transition = transition.previous;
      }

      if(transition != null) {
        transitions.push(transition);
      }

      let nextState = Object.assign({transitions: transitions.reverse()}, state);

      return nextState;
    }
    default: {
      return state;
    }
  }
}
