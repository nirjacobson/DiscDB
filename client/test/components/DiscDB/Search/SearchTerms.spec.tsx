require("client/test/setup.tsx");

// 3rd party
import React from "react";

// test deps
import {mount} from "enzyme";
import chai, {expect} from "chai";
import sinon from "sinon";
import sinonChai from "sinon-chai";

chai.use(sinonChai);

// components
import SearchTerms from "client/src/tsx/components/DiscDB/Search/SearchTerms";
import {Button, Form} from "react-bootstrap";

describe("src/tsx/components/DiscDB/Search/SearchTerms", function () {
    describe("when rendered", function () {
        beforeEach(function () {
            this.sandbox = sinon.createSandbox();
            this.onChangeStub = sinon.stub();
            this.wrapper = mount(<SearchTerms onChange={this.onChangeStub} loading={false} />);
        });

        afterEach(function () {
            this.sandbox.restore();
        });

        it("shows the search fields", function () {
            expect(this.wrapper.find(Form.Control).length).to.equal(4);

            expect(this.wrapper.find(Form.Control).at(0).prop("placeholder")).to.equal("Artist");
            expect(this.wrapper.find(Form.Control).at(1).prop("placeholder")).to.equal("Title");
            expect(this.wrapper.find(Form.Control).at(2).prop("placeholder")).to.equal("Genre");
            expect(this.wrapper.find(Form.Control).at(3).prop("placeholder")).to.equal("Year");
        });

        describe("and the fields are  filled out and 'Search' is clicked", function () {
            beforeEach(function () {
                this.wrapper
                    .find(Form.Control)
                    .at(0)
                    .simulate("change", {target: {value: "rippingtons"}});
                this.wrapper
                    .find(Form.Control)
                    .at(1)
                    .simulate("change", {target: {value: "curves"}});
                this.wrapper
                    .find(Form.Control)
                    .at(2)
                    .simulate("change", {target: {value: "jazz"}});
                this.wrapper
                    .find(Form.Control)
                    .at(3)
                    .simulate("change", {target: {value: 1991}});
                this.wrapper.find(Button).simulate("click");
            });

            it("calls the onChange callback with the appropriate value", function () {
                expect(this.onChangeStub).to.have.been.calledWith({
                    artist: "rippingtons",
                    title: "curves",
                    genre: "jazz",
                    year: 1991
                });
            });
        });
    });
});
