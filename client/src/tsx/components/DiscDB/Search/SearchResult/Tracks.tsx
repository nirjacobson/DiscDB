/** @jsx jsx */

import React from "react";
import {jsx, css} from "@emotion/core";

import {Table} from "react-bootstrap";

import Disc from "src/tsx/Disc";

import {secondsToTimeString, trackLength} from "src/tsx/utils/timeUtils";

const firstCell = css`
    width: 16px;
    text-align: center;
`;

const lastCell = css`
    width: 64px;
    text-align: right;
`;

interface Props {
    disc: Disc;
}

const Tracks = ({disc}: Props): JSX.Element => {
    return (
        <Table>
            <tbody>
                {disc.tracks.map((track, i) => (
                    <tr key={track.frameOffset}>
                        <td css={firstCell}>{i + 1}</td>
                        <td>{track.title}</td>
                        <td css={lastCell}>{secondsToTimeString(trackLength(disc, i))}</td>
                    </tr>
                ))}
            </tbody>
        </Table>
    );
};

export default Tracks;
