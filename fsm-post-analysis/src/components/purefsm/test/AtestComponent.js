'use strict';

import React from 'react';

require('styles/purefsm/test/Atest.scss');

class AtestComponent extends React.Component {
  render() {
    return (
      <div className="atest-component">
        Please edit src/components/purefsm/test//AtestComponent.js to update this component!
      </div>
    );
  }
}

AtestComponent.displayName = 'PurefsmTestAtestComponent';

// Uncomment properties you need
// AtestComponent.propTypes = {};
// AtestComponent.defaultProps = {};

export default AtestComponent;
