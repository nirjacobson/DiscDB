import React, {useState} from 'react';
import styled from '@emotion/styled';

import {Button, Form} from 'react-bootstrap';

const Fields = styled.div`
    display: flex;
`;

const LargeGroup = styled.div`
    width: 250px;
    padding-right: 8px;
`;

const SmallGroup = styled.div`
    width: 150px;
    padding-right: 8px;
`;

const ButtonGroup = styled.div`
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    padding-bottom: 16px;
`;

interface Props {
    onChange: (terms: Record<string, unknown>) => void;
    loading: boolean;
}

const SearchTerms = ({onChange, loading}: Props): JSX.Element => {
    const [artist, setArtist] = useState<string>();
    const [title, setTitle] = useState<string>();
    const [genre, setGenre] = useState<string>();
    const [year, setYear] = useState<number>();

    return (
        <>
            <Fields>
                <LargeGroup>
                    <Form.Group controlId='artistGroup'>
                        <Form.Control
                            type='text'
                            placeholder='Artist'
                            value={artist}
                            onChange={(e) => setArtist(e.target.value)}
                        />
                    </Form.Group>
                </LargeGroup>
                <LargeGroup>
                    <Form.Group controlId='titleGroup'>
                        <Form.Control
                            type='text'
                            placeholder='Title'
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                        />
                    </Form.Group>
                </LargeGroup>
                <SmallGroup>
                    <Form.Group controlId='genreGroup'>
                        <Form.Control
                            type='text'
                            placeholder='Genre'
                            value={genre}
                            onChange={(e) => setGenre(e.target.value)}
                        />
                    </Form.Group>
                </SmallGroup>
                <SmallGroup>
                    <Form.Group controlId='yearGroup'>
                        <Form.Control
                            type='number'
                            placeholder='Year'
                            min={1900}
                            value={year}
                            onChange={(e) => setYear(parseInt(e.target.value))}
                        />
                    </Form.Group>
                </SmallGroup>
                <ButtonGroup>
                    <Button
                        variant='primary'
                        onClick={() =>
                            onChange({
                                artist,
                                title,
                                genre,
                                year
                            })
                        }
                        disabled={loading}
                    >
                        {loading ? 'Loading...' : 'Search'}
                    </Button>
                </ButtonGroup>
            </Fields>
        </>
    );
};

export default SearchTerms;
