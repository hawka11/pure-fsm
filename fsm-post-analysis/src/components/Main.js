require('normalize.css/normalize.css');
require('styles/App.scss');

import React, {
  PropTypes
} from 'react';
import SingleTransition from './SingleTransition';

class AppComponent extends React.Component {

  constructor(props) {
    super(props);
    this.handleAddChildClick = this.handleAddChildClick.bind(this);
  }

  handleAddChildClick(e) {
      e.preventDefault();

      this.props.actions.LoadFsm();
    }

    render() {

        let transitions = this.props.myreducer.transitions || [];

        return (
          <div className="index">
            <input type="button" value="+" onClick={this.handleAddChildClick} />
            <div className="transitions-holder">
              <div className="transitions-scroll">
                {transitions.map((t) => <SingleTransition transition={t} /> )}
              </div>
              <div>&nbsp;</div>
            </div>
          </div>
        );
      }
}

AppComponent.propTypes  = {
  myreducer: PropTypes.object.isRequired
};

export default AppComponent;
