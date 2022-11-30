import React from 'react';
import {render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';

import '@testing-library/jest-dom/extend-expect';

// components
import DiscDB from 'src/tsx/components/DiscDB';

describe('src/tsx/components/DiscDB', function () {
    describe('when rendered', function () {
        beforeEach(function () {
            render(
                <MemoryRouter>
                    <DiscDB />
                </MemoryRouter>
            );
        });

        it('shows 4 links', function () {
            expect(screen.queryAllByRole('link').length).toBe(4);
        });

        it('shows a Home link', function () {
            const links = screen.getAllByRole('link');
            expect(links[0]).toHaveTextContent('Home');
        });

        it('shows an API link', function () {
            const links = screen.getAllByRole('link');
            expect(links[1]).toHaveTextContent('API');
        });

        it('shows a Search link', function () {
            const links = screen.getAllByRole('link');
            expect(links[2]).toHaveTextContent('Search');
        });

        it('shows a Contact link', function () {
            const links = screen.getAllByRole('link');
            expect(links[3]).toHaveTextContent('Contact');
        });
    });
});
