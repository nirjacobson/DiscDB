import React from 'react';
import {fireEvent, render, screen, within} from '@testing-library/react';

import '@testing-library/jest-dom/extend-expect';

// components
import Tracks from 'src/tsx/components/DiscDB/Search/SearchResult/Tracks';
// fixtures
import fixtures from 'src/test/fixtures';
// utils
import {secondsToTimeString, trackLength} from 'src/tsx/utils/timeUtils';

describe('src/tsx/components/DiscDB/Search/SearchResult/Tracks', function () {
    describe('when rendered', function () {
        beforeEach(function () {
            render(<Tracks disc={fixtures.DISC} />);
        });

        it('shows a row for each disc track', function () {
            const rows = screen.queryAllByRole('row');

            expect(rows.length).toBe(fixtures.DISC.tracks.length);

            for (let i = 0; i < fixtures.DISC.tracks.length; i++) {
                const cells = within(rows[i]).queryAllByRole('cell');
                expect(cells.length).toBe(3);
                expect(cells[0]).toHaveTextContent(i + 1 + '');
                expect(cells[1]).toHaveTextContent(fixtures.DISC.tracks[i].title);
                expect(cells[2]).toHaveTextContent(
                    secondsToTimeString(trackLength(fixtures.DISC, i))
                );
            }
        });
    });
});
