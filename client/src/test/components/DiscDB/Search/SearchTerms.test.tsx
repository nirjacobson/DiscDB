import React from 'react';
import {fireEvent, render, screen} from '@testing-library/react';

import '@testing-library/jest-dom';

// components
import SearchTerms from 'src/tsx/components/DiscDB/Search/SearchTerms';

describe('src/tsx/components/DiscDB/Search/SearchTerms', function () {
    describe('when rendered', function () {
        beforeEach(function () {
            this.onChangeStub = jest.fn();
            render(<SearchTerms onChange={this.onChangeStub} loading={false} />);
        });

        it('shows the search fields', function () {
            expect(screen.queryByPlaceholderText('Artist')).toBeInTheDocument();
            expect(screen.queryByPlaceholderText('Title')).toBeInTheDocument();
            expect(screen.queryByPlaceholderText('Genre')).toBeInTheDocument();
            expect(screen.queryByPlaceholderText('Year')).toBeInTheDocument();
        });

        it('shows the search button', function () {
            expect(screen.queryByText('Search')).toBeInTheDocument();
            expect(screen.queryByText('Search')).toHaveClass('btn-primary');
        });

        describe('and the fields are filled out and "Search" is clicked', function () {
            beforeEach(function () {
                fireEvent.change(screen.getByPlaceholderText('Artist'), {
                    target: {value: 'rippingtons'}
                });
                fireEvent.change(screen.getByPlaceholderText('Title'), {target: {value: 'curves'}});
                fireEvent.change(screen.getByPlaceholderText('Genre'), {target: {value: 'jazz'}});
                fireEvent.change(screen.getByPlaceholderText('Year'), {target: {value: 1991}});
                fireEvent.click(screen.getByText('Search'));
            });

            it('calls the onChange callback with the appropriate value', function () {
                expect(this.onChangeStub).toHaveBeenCalledWith({
                    artist: 'rippingtons',
                    title: 'curves',
                    genre: 'jazz',
                    year: 1991
                });
            });
        });
    });
});
