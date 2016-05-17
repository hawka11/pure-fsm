require('normalize.css/normalize.css');
require('styles/App.css');

import React, {
  Component,
  PropTypes
} from 'react';
import ATestC from './purefsm/test/AtestComponent';

let yeomanImage = require('../images/yeoman.png');

class AppComponent extends React.Component {

  constructor(props) {
    super(props);
    this.handleAddChildClick = this.handleAddChildClick.bind(this);
  }

  handleAddChildClick(e) {
      e.preventDefault();

      const { LoadFsm } = this.props.actions;
      LoadFsm('fromcomponentactionparam', 777);
    }

    render() {

        const { myreducer } = this.props;
        let transition = myreducer.transition || {event: ''};

        return (
          <div className="index">
            <img src={yeomanImage} alt="Yeoman Generator" />
            <div className="notice">Please edit <code>src/components/Main.js</code> to get started! keith</div>
            <div>{ transition.event }</div>
            <input type="button" value="+" onClick={this.handleAddChildClick} />
            <ATestC />
          </div>
        );
      }
}

AppComponent.propTypes  = {
  myreducer: PropTypes.object.isRequired,
};

export default AppComponent;
