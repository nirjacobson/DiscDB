require("client/test/setup.tsx");

// 3rd party
import React from "react";
// test deps
import {mount} from "enzyme";
import chai, {expect} from "chai";
import sinon from "sinon";
import sinonChai from "sinon-chai";
// components
import Pages from "client/src/tsx/components/DiscDB/Search/Pages";
import {Button} from "react-bootstrap";

chai.use(sinonChai);

describe("src/tsx/components/DiscDB/Search/Pages", function () {
    describe("when rendered for fewer than 10 pages", function () {
        beforeEach(function () {
            this.sandbox = sinon.createSandbox();
            this.onChangeStub = sinon.stub();
            this.page = 3;
            this.pages = 7;
            this.wrapper = mount(
                <Pages onChange={this.onChangeStub} page={this.page} pages={this.pages} />
            );
        });

        afterEach(function () {
            this.sandbox.restore();
        });

        it("shows a button for each page", function () {
            expect(this.wrapper.find(Button).length).to.equal(this.pages);
            for (let i = 0; i < this.pages; i++) {
                expect(this.wrapper.find(Button).at(i).text()).to.equal("" + (i + 1));
                expect(this.wrapper.find(Button).at(i).prop("variant")).to.equal(
                    i === this.page - 1 ? "primary" : "default"
                );
            }
        });

        describe("and a page is clicked", function () {
            beforeEach(function () {
                this.wrapper.find(Button).at(1).simulate("click");
            });

            it("calls the onChange callback with the appropriate value", function () {
                expect(this.onChangeStub).to.have.been.calledWith(2);
            });
        });
    });

    describe("when rendered for more than 10 pages, current page less than 10", function () {
        beforeEach(function () {
            this.sandbox = sinon.createSandbox();
            this.onChangeStub = sinon.stub();
            this.page = 3;
            this.pages = 16;
            this.wrapper = mount(
                <Pages onChange={this.onChangeStub} page={this.page} pages={this.pages} />
            );
        });

        afterEach(function () {
            this.sandbox.restore();
        });

        it("shows a button for the first 10 pages", function () {
            for (let i = 0; i < 10; i++) {
                expect(this.wrapper.find(Button).at(i).text()).to.equal("" + (i + 1));
                expect(this.wrapper.find(Button).at(i).prop("variant")).to.equal(
                    i === this.page - 1 ? "primary" : "default"
                );
            }
        });

        it("shows a button for the next set of pages", function () {
            expect(this.wrapper.find(Button).at(10).text()).to.equal(">");
            expect(this.wrapper.find(Button).at(10).prop("variant")).to.equal("default");
        });

        it("shows a button for the last page", function () {
            expect(this.wrapper.find(Button).last().text()).to.equal("" + this.pages);
            expect(this.wrapper.find(Button).last().prop("variant")).to.equal("default");
        });

        describe("and a page is clicked", function () {
            beforeEach(function () {
                this.wrapper.find(Button).at(1).simulate("click");
            });

            it("calls the onChange callback with the appropriate value", function () {
                expect(this.onChangeStub).to.have.been.calledWith(2);
            });
        });

        describe("and the next button is clicked", function () {
            beforeEach(function () {
                this.wrapper.find(Button).at(10).simulate("click");
            });

            it("calls the onChange callback with the appropriate value", function () {
                expect(this.onChangeStub).to.have.been.calledWith(11);
            });
        });

        describe("and the last page is clicked", function () {
            beforeEach(function () {
                this.wrapper.find(Button).last().simulate("click");
            });

            it("calls the onChange callback with the appropriate value", function () {
                expect(this.onChangeStub).to.have.been.calledWith(this.pages);
            });
        });
    });

    describe("when rendered for more than 10 pages, current page greater than 10", function () {
        beforeEach(function () {
            this.sandbox = sinon.createSandbox();
            this.onChangeStub = sinon.stub();
            this.page = 13;
            this.pages = 16;
            this.wrapper = mount(
                <Pages onChange={this.onChangeStub} page={this.page} pages={this.pages} />
            );
        });

        afterEach(function () {
            this.sandbox.restore();
        });

        it("shows a button for the previous set of pages", function () {
            expect(this.wrapper.find(Button).at(0).text()).to.equal("<");
            expect(this.wrapper.find(Button).at(0).prop("variant")).to.equal("default");
        });

        it("shows a button for pages 11 on", function () {
            for (let i = 1; i < this.pages - 10 + 1; i++) {
                expect(this.wrapper.find(Button).at(i).text()).to.equal("" + (i + 10));
                expect(this.wrapper.find(Button).at(i).prop("variant")).to.equal(
                    i === this.page - 10 ? "primary" : "default"
                );
            }
        });

        describe("and the previous button is clicked", function () {
            beforeEach(function () {
                this.wrapper.find(Button).at(0).simulate("click");
            });

            it("calls the onChange callback with the appropriate value", function () {
                expect(this.onChangeStub).to.have.been.calledWith(10);
            });
        });

        describe("and a page is clicked", function () {
            beforeEach(function () {
                this.wrapper.find(Button).at(1).simulate("click");
            });

            it("calls the onChange callback with the appropriate value", function () {
                expect(this.onChangeStub).to.have.been.calledWith(11);
            });
        });
    });

    describe("when rendered for more than 30 pages, current page greater than 20 and less than 30", function () {
        beforeEach(function () {
            this.sandbox = sinon.createSandbox();
            this.onChangeStub = sinon.stub();
            this.page = 23;
            this.pages = 36;
            this.wrapper = mount(
                <Pages onChange={this.onChangeStub} page={this.page} pages={this.pages} />
            );
        });

        afterEach(function () {
            this.sandbox.restore();
        });

        it("shows a button for the previous set of pages", function () {
            expect(this.wrapper.find(Button).at(0).text()).to.equal("<");
            expect(this.wrapper.find(Button).at(0).prop("variant")).to.equal("default");
        });

        it("shows a button for pages 21 to 30", function () {
            for (let i = 1; i < 10 + 1; i++) {
                expect(this.wrapper.find(Button).at(i).text()).to.equal("" + (i + 20));
                expect(this.wrapper.find(Button).at(i).prop("variant")).to.equal(
                    i === this.page - 20 ? "primary" : "default"
                );
            }
        });

        it("shows a button for the next set of pages", function () {
            expect(this.wrapper.find(Button).at(11).text()).to.equal(">");
            expect(this.wrapper.find(Button).at(11).prop("variant")).to.equal("default");
        });

        it("shows a button for the last page", function () {
            expect(this.wrapper.find(Button).last().text()).to.equal("" + this.pages);
            expect(this.wrapper.find(Button).last().prop("variant")).to.equal("default");
        });

        describe("and the previous button is clicked", function () {
            beforeEach(function () {
                this.wrapper.find(Button).at(0).simulate("click");
            });

            it("calls the onChange callback with the appropriate value", function () {
                expect(this.onChangeStub).to.have.been.calledWith(20);
            });
        });

        describe("and a page is clicked", function () {
            beforeEach(function () {
                this.wrapper.find(Button).at(1).simulate("click");
            });

            it("calls the onChange callback with the appropriate value", function () {
                expect(this.onChangeStub).to.have.been.calledWith(21);
            });
        });

        describe("and the next button is clicked", function () {
            beforeEach(function () {
                this.wrapper.find(Button).at(11).simulate("click");
            });

            it("calls the onChange callback with the appropriate value", function () {
                expect(this.onChangeStub).to.have.been.calledWith(31);
            });
        });

        describe("and the last page is clicked", function () {
            beforeEach(function () {
                this.wrapper.find(Button).last().simulate("click");
            });

            it("calls the onChange callback with the appropriate value", function () {
                expect(this.onChangeStub).to.have.been.calledWith(this.pages);
            });
        });
    });
});
