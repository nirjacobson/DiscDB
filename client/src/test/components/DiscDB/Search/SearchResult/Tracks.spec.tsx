require("src/test/setup.tsx");

// 3rd party
import React from "react";
// test deps
import {mount} from "enzyme";
import {expect} from "chai";
// components
import Tracks from "src/tsx/components/DiscDB/Search/SearchResult/Tracks";
// fixtures
import fixtures from "src/test/fixtures";

describe("src/tsx/components/DiscDB/Search/SearchResult/Tracks", function () {
    describe("when rendered", function () {
        beforeEach(function () {
            this.wrapper = mount(<Tracks disc={fixtures.DISC} />);
        });

        it("shows a row for each disc track", function () {
            expect(this.wrapper.find("tr").length).to.equal(fixtures.DISC.tracks.length);

            for (let i = 0; i < fixtures.DISC.tracks.length; i++) {
                expect(this.wrapper.find("tr").at(i).find("td").at(0).text()).to.equal(
                    "" + (i + 1)
                );
                expect(this.wrapper.find("tr").at(i).find("td").at(1).text()).to.equal(
                    fixtures.DISC.tracks[i].title
                );
            }
        });
    });
});
