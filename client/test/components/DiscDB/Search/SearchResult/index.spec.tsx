require("client/test/setup.tsx");

// 3rd party
import React from "react";
// test deps
import {mount} from "enzyme";
import {expect} from "chai";
// components
import SearchResult from "client/src/tsx/components/DiscDB/Search/SearchResult";
import Tracks from "client/src/tsx/components/DiscDB/Search/SearchResult/Tracks";
// utils
import {secondsToTimeString} from "client/src/tsx/utils/timeUtils";
// fixtures
import fixtures from "client/test/fixtures";

describe("src/tsx/components/DiscDB/Search/SearchResult", function () {
    describe("when rendered", function () {
        beforeEach(function () {
            this.wrapper = mount(<SearchResult result={fixtures.DISC} />);
        });

        it("shows the ID", function () {
            expect(this.wrapper.find("tr").at(0).find("td").at(1).text()).to.equal(
                fixtures.DISC.id
            );
        });

        it("shows the disc ID", function () {
            expect(this.wrapper.find("tr").at(1).find("td").at(1).text()).to.equal(
                fixtures.DISC.discId
            );
        });

        it("shows the artist", function () {
            expect(this.wrapper.find("tr").at(2).find("td").at(1).text()).to.equal(
                fixtures.DISC.artist
            );
        });

        it("shows the title", function () {
            expect(this.wrapper.find("tr").at(3).find("td").at(1).text()).to.equal(
                fixtures.DISC.title
            );
        });

        it("shows the genre", function () {
            expect(this.wrapper.find("tr").at(4).find("td").at(1).text()).to.equal(
                fixtures.DISC.genre
            );
        });

        it("shows the year", function () {
            expect(this.wrapper.find("tr").at(5).find("td").at(1).text()).to.equal(
                "" + fixtures.DISC.year
            );
        });

        it("shows the length", function () {
            expect(this.wrapper.find("tr").at(6).find("td").at(1).text()).to.equal(
                secondsToTimeString(fixtures.DISC.length)
            );
        });

        it("shows the tracks", function () {
            expect(this.wrapper.find("tr").at(7).find("td").at(1).find(Tracks).length).to.equal(1);
        });
    });
});
