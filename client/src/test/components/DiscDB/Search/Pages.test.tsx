import React from 'react';
import {fireEvent, render, screen} from '@testing-library/react';

import '@testing-library/jest-dom';

// components
import Pages from 'src/tsx/components/DiscDB/Search/Pages';

describe('src/tsx/components/DiscDB/Search/Pages', function () {
    describe('when rendered for fewer than 10 pages', function () {
        beforeEach(function () {
            this.onChangeStub = jest.fn();
            this.page = 3;
            this.pages = 7;
            render(<Pages onChange={this.onChangeStub} page={this.page} pages={this.pages} />);
        });

        it('shows a button for each page', function () {
            const buttons = screen.queryAllByRole('button');
            expect(buttons.length).toBe(this.pages);
            for (let i = 0; i < this.pages; i++) {
                expect(buttons[i].textContent).toBe(i + 1 + '');
                expect(buttons[i]).toHaveClass(i + 1 === this.page ? 'btn-primary' : 'btn-default');
            }
        });

        describe('and a page is clicked', function () {
            beforeEach(function () {
                fireEvent.click(screen.getAllByRole('button')[1]);
            });

            it('calls the onChange callback with the appropriate value', function () {
                expect(this.onChangeStub).toHaveBeenCalledWith(2);
            });
        });
    });

    describe('when rendered for more than 10 pages, current page less than 10', function () {
        beforeEach(function () {
            this.onChangeStub = jest.fn();
            this.page = 3;
            this.pages = 16;
            render(<Pages onChange={this.onChangeStub} page={this.page} pages={this.pages} />);
        });

        it('shows buttons for the first 10 pages', function () {
            const buttons = screen.queryAllByRole('button');
            for (let i = 0; i < 10; i++) {
                expect(buttons[i].textContent).toBe(i + 1 + '');
                expect(buttons[i]).toHaveClass(i + 1 === this.page ? 'btn-primary' : 'btn-default');
            }
        });

        it('shows a button for the next set of pages', function () {
            expect(screen.getAllByRole('button')[10].textContent).toBe('>');
            expect(screen.getAllByRole('button')[10]).toHaveClass('btn-default');
        });

        it('shows a button for the last page', function () {
            const lastButton = screen.getAllByRole('button').reduce((acc, curr) => curr);
            expect(lastButton.textContent).toBe(this.pages + '');
            expect(lastButton).toHaveClass('btn-default');
        });

        describe('and a page is clicked', function () {
            beforeEach(function () {
                fireEvent.click(screen.getAllByRole('button')[1]);
            });

            it('calls the onChange callback with the appropriate value', function () {
                expect(this.onChangeStub).toHaveBeenCalledWith(2);
            });
        });

        describe('and the next button is clicked', function () {
            beforeEach(function () {
                fireEvent.click(screen.getAllByRole('button')[10]);
            });

            it('calls the onChange callback with the appropriate value', function () {
                expect(this.onChangeStub).toHaveBeenCalledWith(11);
            });
        });

        describe('and the last page is clicked', function () {
            beforeEach(function () {
                const lastButton = screen.getAllByRole('button').reduce((acc, curr) => curr);
                fireEvent.click(lastButton);
            });

            it('calls the onChange callback with the appropriate value', function () {
                expect(this.onChangeStub).toHaveBeenCalledWith(this.pages);
            });
        });
    });

    describe('when rendered for more than 10 pages, current page greater than 10', function () {
        beforeEach(function () {
            this.onChangeStub = jest.fn();
            this.page = 13;
            this.pages = 16;
            render(<Pages onChange={this.onChangeStub} page={this.page} pages={this.pages} />);
        });

        it('shows a button for the previous set of pages', function () {
            expect(screen.getAllByRole('button')[0].textContent).toBe('<');
            expect(screen.getAllByRole('button')[0]).toHaveClass('btn-default');
        });

        it('shows a button for pages 11 on', function () {
            for (let i = 1; i < this.pages - 10 + 1; i++) {
                expect(screen.getAllByRole('button')[i].textContent).toBe(i + 10 + '');
                expect(screen.getAllByRole('button')[i]).toHaveClass(
                    i === this.page - 10 ? 'btn-primary' : 'btn-default'
                );
            }
        });

        describe('and the previous button is clicked', function () {
            beforeEach(function () {
                fireEvent.click(screen.getAllByRole('button')[0]);
            });

            it('calls the onChange callback with the appropriate value', function () {
                expect(this.onChangeStub).toHaveBeenCalledWith(10);
            });
        });

        describe('and a page is clicked', function () {
            beforeEach(function () {
                fireEvent.click(screen.getAllByRole('button')[1]);
            });

            it('calls the onChange callback with the appropriate value', function () {
                expect(this.onChangeStub).toHaveBeenCalledWith(11);
            });
        });
    });

    describe('when rendered for more than 30 pages, current page greater than 20 and less than 30', function () {
        beforeEach(function () {
            this.onChangeStub = jest.fn();
            this.page = 23;
            this.pages = 36;
            render(<Pages onChange={this.onChangeStub} page={this.page} pages={this.pages} />);
        });

        it('shows a button for the previous set of pages', function () {
            expect(screen.getAllByRole('button')[0].textContent).toBe('<');
            expect(screen.getAllByRole('button')[0]).toHaveClass('btn-default');
        });

        it('shows a button for pages 21 to 30', function () {
            for (let i = 1; i < 10 + 1; i++) {
                expect(screen.getAllByRole('button')[i].textContent).toBe(i + 20 + '');
                expect(screen.getAllByRole('button')[i]).toHaveClass(
                    i === this.page - 20 ? 'btn-primary' : 'btn-default'
                );
            }
        });

        it('shows a button for the next set of pages', function () {
            expect(screen.getAllByRole('button')[11].textContent).toBe('>');
            expect(screen.getAllByRole('button')[11]).toHaveClass('btn-default');
        });

        it('shows a button for the last page', function () {
            const lastButton = screen.getAllByRole('button').reduce((acc, curr) => curr);
            expect(lastButton.textContent).toBe(this.pages + '');
            expect(lastButton).toHaveClass('btn-default');
        });

        describe('and the previous button is clicked', function () {
            beforeEach(function () {
                fireEvent.click(screen.getAllByRole('button')[0]);
            });

            it('calls the onChange callback with the appropriate value', function () {
                expect(this.onChangeStub).toHaveBeenCalledWith(20);
            });
        });

        describe('and a page is clicked', function () {
            beforeEach(function () {
                fireEvent.click(screen.getAllByRole('button')[1]);
            });

            it('calls the onChange callback with the appropriate value', function () {
                expect(this.onChangeStub).toHaveBeenCalledWith(21);
            });
        });

        describe('and the next button is clicked', function () {
            beforeEach(function () {
                fireEvent.click(screen.getAllByRole('button')[11]);
            });

            it('calls the onChange callback with the appropriate value', function () {
                expect(this.onChangeStub).toHaveBeenCalledWith(31);
            });
        });

        describe('and the last page is clicked', function () {
            beforeEach(function () {
                const lastButton = screen.getAllByRole('button').reduce((acc, curr) => curr);
                fireEvent.click(lastButton);
            });

            it('calls the onChange callback with the appropriate value', function () {
                expect(this.onChangeStub).toHaveBeenCalledWith(this.pages);
            });
        });
    });
});
