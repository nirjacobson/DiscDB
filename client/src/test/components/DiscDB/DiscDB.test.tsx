import React from 'react';
import {render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';

// components
import DiscDB from 'src/tsx/components/DiscDB';

describe('src/tsx/components/DiscDB', function () {
    describe('when rendered', function () {
        beforeAll(function () {
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
    });
});
