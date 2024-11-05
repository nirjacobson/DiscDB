import React from 'react';
import {render, screen, within} from '@testing-library/react';

import '@testing-library/jest-dom';

// components
import SearchResult from 'src/tsx/components/DiscDB/Search/SearchResult';
// utils
import {secondsToTimeString} from 'src/tsx/utils/timeUtils';
// fixtures
import fixtures from 'src/test/fixtures';

describe('src/tsx/components/DiscDB/Search/SearchResult', function () {
    describe('when rendered', function () {
        beforeEach(function () {
            render(<SearchResult result={fixtures.DISC} />);
        });

        it('shows the ID', function () {
            expect(within(screen.getAllByRole('row')[0]).getAllByRole('cell')[0]).toHaveTextContent(
                'ID'
            );
            expect(within(screen.getAllByRole('row')[0]).getAllByRole('cell')[1]).toHaveTextContent(
                fixtures.DISC.id
            );
        });

        it('shows the disc ID', function () {
            expect(within(screen.getAllByRole('row')[1]).getAllByRole('cell')[0]).toHaveTextContent(
                'Disc ID'
            );
            expect(within(screen.getAllByRole('row')[1]).getAllByRole('cell')[1]).toHaveTextContent(
                fixtures.DISC.discId
            );
        });

        it('shows the artist', function () {
            expect(within(screen.getAllByRole('row')[2]).getAllByRole('cell')[0]).toHaveTextContent(
                'Artist'
            );
            expect(within(screen.getAllByRole('row')[2]).getAllByRole('cell')[1]).toHaveTextContent(
                fixtures.DISC.artist
            );
        });

        it('shows the title', function () {
            expect(within(screen.getAllByRole('row')[3]).getAllByRole('cell')[0]).toHaveTextContent(
                'Title'
            );
            expect(within(screen.getAllByRole('row')[3]).getAllByRole('cell')[1]).toHaveTextContent(
                fixtures.DISC.title
            );
        });

        it('shows the genre', function () {
            expect(within(screen.getAllByRole('row')[4]).getAllByRole('cell')[0]).toHaveTextContent(
                'Genre'
            );
            expect(within(screen.getAllByRole('row')[4]).getAllByRole('cell')[1]).toHaveTextContent(
                fixtures.DISC.genre
            );
        });

        it('shows the year', function () {
            expect(within(screen.getAllByRole('row')[5]).getAllByRole('cell')[0]).toHaveTextContent(
                'Year'
            );
            expect(within(screen.getAllByRole('row')[5]).getAllByRole('cell')[1]).toHaveTextContent(
                fixtures.DISC.year + ''
            );
        });

        it('shows the length', function () {
            expect(within(screen.getAllByRole('row')[6]).getAllByRole('cell')[0]).toHaveTextContent(
                'Length'
            );
            expect(within(screen.getAllByRole('row')[6]).getAllByRole('cell')[1]).toHaveTextContent(
                secondsToTimeString(fixtures.DISC.length)
            );
        });

        it('shows the tracks', function () {
            expect(within(screen.getAllByRole('row')[7]).getAllByRole('cell')[0]).toHaveTextContent(
                'Tracks'
            );
        });
    });
});
