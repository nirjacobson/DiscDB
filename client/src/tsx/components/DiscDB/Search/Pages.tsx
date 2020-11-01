/** @jsx jsx */

import React from "react";
import {jsx, css} from "@emotion/core";

import {Button, ButtonGroup, ButtonToolbar} from "react-bootstrap";

const toolbar = css`
    padding-bottom: 8px;
`;

interface Props {
    page: number;
    pages: number;
    onChange: (number) => void;
}

const Pages = ({page, pages, onChange}: Props): JSX.Element => {
    const tenIndex = Math.floor((page - 1) / 10);
    const left = 10 * tenIndex + 1;
    const right = pages < left + 9 ? pages : left + 9;
    const end = pages > right ? pages : undefined;
    const showLeftArrow = left > 1;
    const showRightArrow = !!end;

    return (
        <ButtonToolbar css={toolbar}>
            <ButtonGroup className="mr-2">
                {showLeftArrow && (
                    <Button key="back" variant="default" onClick={() => onChange(left - 1)}>
                        &lt;
                    </Button>
                )}
                {Array.from(Array(right + 1).keys())
                    .slice(left)
                    .map((pageNum) => (
                        <Button
                            key={pageNum}
                            variant={pageNum === page ? "primary" : "default"}
                            onClick={() => onChange(pageNum)}
                        >
                            {pageNum}
                        </Button>
                    ))}
                {showRightArrow && (
                    <Button key="next" variant="default" onClick={() => onChange(right + 1)}>
                        &gt;
                    </Button>
                )}
            </ButtonGroup>
            {end && (
                <span>
                    ...
                    <ButtonGroup className="mr-2">
                        <Button key="back" variant="default" onClick={() => onChange(pages)}>
                            {end}
                        </Button>
                    </ButtonGroup>
                </span>
            )}
        </ButtonToolbar>
    );
};

export default Pages;
