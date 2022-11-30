import React from 'react';
import {BrowserRouter as Router} from 'react-router-dom';

import DiscDB from 'src/tsx/components/DiscDB';

const App = (): JSX.Element => (
    <Router>
        <DiscDB />
    </Router>
);

export default App;
