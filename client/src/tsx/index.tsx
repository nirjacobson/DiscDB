import React from "react";
import {createRoot} from "react-dom/client";

import App from "./App";

import "bootstrap/dist/css/bootstrap.css";
import "/public/index.less";

const root = createRoot(document.getElementById("root"));
root.render(<App />);
