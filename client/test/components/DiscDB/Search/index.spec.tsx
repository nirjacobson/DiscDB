require("client/test/setup.tsx");

// 3rd party
import React from "react";
// test deps
import {mount} from "enzyme";
import chai, {expect} from "chai";
import sinon from "sinon";
import sinonChai from "sinon-chai";
// components
import Search from "client/src/tsx/components/DiscDB/Search";
import SearchTerms from "client/src/tsx/components/DiscDB/Search/SearchTerms";
import Pages from "client/src/tsx/components/DiscDB/Search/Pages";
import SearchResult from "client/src/tsx/components/DiscDB/Search/SearchResult";
import {Button} from "react-bootstrap";
// api
import api from "client/src/tsx/api";
// fixtures
import fixtures from "client/test/fixtures";

chai.use(sinonChai);

describe("src/tsx/components/DiscDB/Search", function () {
    describe("when rendered", function () {
        beforeEach(function () {
            this.sandbox = sinon.createSandbox();
            this.findStub = sinon.stub(api, "find");
            this.findStub.returns(
                new Promise((res, _) => {
                    res({
                        pages: 1,
                        results: [fixtures.DISC]
                    });
                })
            );
            this.wrapper = mount(<Search />);
        });

        afterEach(function () {
            this.findStub.restore();
            this.sandbox.restore();
        });

        it("shows the search fields", function () {
            expect(this.wrapper.find(SearchTerms).length).to.equal(1);
        });

        it("does not show the paginator", function () {
            expect(this.wrapper.find(Pages).length).to.equal(0);
        });

        it("does not show search results", function () {
            expect(this.wrapper.find(SearchResult).length).to.equal(0);
        });

        describe("and results are loaded", function () {
            beforeEach(function () {
                this.wrapper.find(SearchTerms).find(Button).simulate("click");
                return Promise.resolve().then(() => this.wrapper.update());
            });

            it("shows the search fields", function () {
                expect(this.wrapper.find(SearchTerms).length).to.equal(1);
            });

            it("shows the paginator", function () {
                expect(this.wrapper.find(Pages).length).to.equal(2);
            });

            it("shows search results", function () {
                expect(this.wrapper.find(SearchResult).length).to.be.greaterThan(0);
            });
        });
    });
});
