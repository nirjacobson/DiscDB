require("src/test/setup.tsx");

// 3rd party
import React from "react";
// test deps
import {mount} from "enzyme";
import {expect} from "chai";
// components
import DiscDB from "src/tsx/components/DiscDB";
import Home from "src/tsx/components/DiscDB/Home";
import API from "src/tsx/components/DiscDB/API";
import Search from "src/tsx/components/DiscDB/Search/index";
import Contact from "src/tsx/components/DiscDB/Contact";
import {Link, MemoryRouter, Route, Switch} from "react-router-dom";

describe("src/tsx/components/DiscDB", function () {
    describe("when rendered", function () {
        beforeEach(function () {
            this.wrapper = mount(
                <MemoryRouter>
                    <DiscDB />
                </MemoryRouter>
            );
        });

        it("shows 4 links", function () {
            expect(this.wrapper.find(Link).length).to.equal(4);
        });

        it("shows a Home link", function () {
            expect(this.wrapper.find(Link).at(0).text()).to.equal("Home");
            expect(this.wrapper.find(Link).at(0).prop("to")).to.equal("/");
        });

        it("shows an API link", function () {
            expect(this.wrapper.find(Link).at(1).text()).to.equal("API");
            expect(this.wrapper.find(Link).at(1).prop("to")).to.equal("/api");
        });

        it("shows a Search link", function () {
            expect(this.wrapper.find(Link).at(2).text()).to.equal("Search");
            expect(this.wrapper.find(Link).at(2).prop("to")).to.equal("/search");
        });

        it("shows a Contact link", function () {
            expect(this.wrapper.find(Link).at(3).text()).to.equal("Contact");
            expect(this.wrapper.find(Link).at(3).prop("to")).to.equal("/contact");
        });
    });

    describe("when rendered under the / path", function () {
        beforeEach(function () {
            this.wrapper = mount(
                <MemoryRouter initialEntries={["/"]}>
                    <DiscDB />
                </MemoryRouter>
            );
        });

        it("shows the Home component", function () {
            expect(this.wrapper.find(Home).length).to.equal(1);
            expect(this.wrapper.find(API).length).to.equal(0);
            expect(this.wrapper.find(Search).length).to.equal(0);
            expect(this.wrapper.find(Contact).length).to.equal(0);
        });
    });

    describe("when rendered under the /api path", function () {
        beforeEach(function () {
            this.wrapper = mount(
                <MemoryRouter initialEntries={["/api"]}>
                    <DiscDB />
                </MemoryRouter>
            );
        });

        it("shows the API component", function () {
            expect(this.wrapper.find(Home).length).to.equal(0);
            expect(this.wrapper.find(API).length).to.equal(1);
            expect(this.wrapper.find(Search).length).to.equal(0);
            expect(this.wrapper.find(Contact).length).to.equal(0);
        });
    });

    describe("when rendered under the /search path", function () {
        beforeEach(function () {
            this.wrapper = mount(
                <MemoryRouter initialEntries={["/search"]}>
                    <DiscDB />
                </MemoryRouter>
            );
        });

        it("shows the Search component", function () {
            expect(this.wrapper.find(Home).length).to.equal(0);
            expect(this.wrapper.find(API).length).to.equal(0);
            expect(this.wrapper.find(Search).length).to.equal(1);
            expect(this.wrapper.find(Contact).length).to.equal(0);
        });
    });

    describe("when rendered under the /contact path", function () {
        beforeEach(function () {
            this.wrapper = mount(
                <MemoryRouter initialEntries={["/contact"]}>
                    <DiscDB />
                </MemoryRouter>
            );
        });

        it("shows the Contact component", function () {
            expect(this.wrapper.find(Home).length).to.equal(0);
            expect(this.wrapper.find(API).length).to.equal(0);
            expect(this.wrapper.find(Search).length).to.equal(0);
            expect(this.wrapper.find(Contact).length).to.equal(1);
        });
    });
});
