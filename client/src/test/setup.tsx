import {configure} from "enzyme";
import Adapter from "enzyme-adapter-react-16";

configure({adapter: new Adapter()});

const {JSDOM} = require("jsdom");

const jsdom = new JSDOM("<!doctype html><html><body></body></html>");
const {window} = jsdom;

function copyProps(src, target) {
    Object.defineProperties(target, {
        ...Object.getOwnPropertyDescriptors(src),
        ...Object.getOwnPropertyDescriptors(target)
    });
}

global.window = window;
global.document = window.document;

copyProps(window, global);
