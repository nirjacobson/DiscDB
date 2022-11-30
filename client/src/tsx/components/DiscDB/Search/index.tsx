import React, {useState} from 'react';
import styled from '@emotion/styled';

import Disc from 'src/tsx/Disc';
import SearchTerms from './SearchTerms';
import Pages from './Pages';
import SearchResult from './SearchResult';

import api from 'src/tsx/api';

const Page = styled.div``;

const Search = (): JSX.Element => {
    const [terms, setTerms] = useState();
    const [page, setPage] = useState<number>(1);
    const [pages, setPages] = useState<number>(0);
    const [results, setResults] = useState<Array<Disc>>([]);
    const [loading, setLoading] = useState<boolean>(false);

    const doFetch = (terms, page) => {
        setLoading(true);

        api.find(terms, page).then((data) => {
            setResults(data.results);
            setPage(page);
            setPages(data.pages);
            setLoading(false);
        });
    };

    const onSearchTermsChange = (terms) => {
        setTerms(terms);
        doFetch(terms, 1);
    };

    const onPageChange = (page) => {
        doFetch(terms, page);
    };

    return (
        <Page>
            <SearchTerms onChange={onSearchTermsChange} loading={loading} />
            {pages > 0 && <Pages page={page} pages={pages} onChange={onPageChange} />}
            {results.map((result) => (
                <SearchResult key={result.id} result={result} />
            ))}
            {pages > 0 && <Pages page={page} pages={pages} onChange={onPageChange} />}
        </Page>
    );
};

export default Search;
