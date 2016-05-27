'use strict';

import React from 'react';

require('styles/SingleTransition.scss');

class SingleTransition extends React.Component {
  render() {
    return (
      <div className="single-transition-component">
        <div><span>Event Received:</span><span>{this.props.transition.event}</span></div>
        <div><span>Current State:</span><span>{this.props.transition.state}</span></div>
        <div><span>Date/Time Received:</span><span>{this.props.transition.transitioned}</span></div>
      </div>
    );
  }
}

SingleTransition.displayName = 'SingleTransition';

// TODO: find out more about below props
// AtestComponent.propTypes = {};
// AtestComponent.defaultProps = {};

export default SingleTransition;
