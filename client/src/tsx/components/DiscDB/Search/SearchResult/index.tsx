/** @jsx jsx */

import React, {useState} from "react";
import styled from "@emotion/styled";
import {jsx, css} from "@emotion/core";

import {Accordion, Button, Card, Table} from "react-bootstrap";

import Disc from "src/tsx/Disc";
import Tracks from "./Tracks";

import {secondsToTimeString} from "src/tsx/utils/timeUtils";

const Result = styled.div`
    padding-bottom: 16px;
`;

const firstCell = css`
    width: 150px;
`;

const Title = styled.div`
    font-size: 24px;
`;

const Artist = styled.div``;

const Json = styled.pre`
    margin-left: 12px;
`;

const toggle = css`
    text-align: left;
`;

interface Props {
    result: Disc;
}

const SearchResult = ({result: disc}: Props): JSX.Element => {
    const [displayJson, setDisplayJson] = useState<boolean>(false);

    const table = (
        <Table>
            <tbody>
                <tr>
                    <td css={firstCell}>
                        <b>ID</b>
                    </td>
                    <td>{disc.id}</td>
                </tr>
                <tr>
                    <td css={firstCell}>
                        <b>Disc ID</b>
                    </td>
                    <td>{disc.discId}</td>
                </tr>
                <tr>
                    <td css={firstCell}>
                        <b>Artist</b>
                    </td>
                    <td>{disc.artist}</td>
                </tr>
                <tr>
                    <td css={firstCell}>
                        <b>Title</b>
                    </td>
                    <td>{disc.title}</td>
                </tr>
                <tr>
                    <td css={firstCell}>
                        <b>Genre</b>
                    </td>
                    <td>{disc.genre}</td>
                </tr>
                <tr>
                    <td css={firstCell}>
                        <b>Year</b>
                    </td>
                    <td>{disc.year}</td>
                </tr>
                <tr>
                    <td css={firstCell}>
                        <b>Length</b>
                    </td>
                    <td>{secondsToTimeString(disc.length)}</td>
                </tr>
                <tr>
                    <td css={firstCell}>
                        <b>Tracks</b>
                    </td>
                    <td>
                        <Tracks disc={disc} />
                    </td>
                </tr>
            </tbody>
        </Table>
    );

    const json = <Json>{JSON.stringify(disc, null, 4)}</Json>;

    return (
        <Result>
            <Accordion>
                <Card>
                    <Card.Header>
                        <Accordion.Toggle as={Button} variant="link" eventKey="0" css={toggle}>
                            <Title>{disc.title}</Title>
                            <Artist>{disc.artist}</Artist>
                        </Accordion.Toggle>
                    </Card.Header>
                    <Accordion.Collapse eventKey="0">
                        <Card.Body>
                            <Button variant="link" onClick={() => setDisplayJson(!displayJson)}>
                                View {displayJson ? "Table" : "JSON"}
                            </Button>
                            {displayJson ? json : table}
                        </Card.Body>
                    </Accordion.Collapse>
                </Card>
            </Accordion>
        </Result>
    );
};

export default SearchResult;
