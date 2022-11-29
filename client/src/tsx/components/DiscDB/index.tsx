import React from "react";
import styled from "@emotion/styled";

import {Link, Route, Routes} from "react-router-dom";

import Home from "./Home";
import API from "./API";
import Search from "./Search/index";
import Contact from "./Contact";

const Header = styled.div`
    display: flex;
    align-items: center;
    padding-left: 16px;
    width: 100%;
    height: 224px;
    background-color: lightgray;
`;

const Title = styled.div`
    font-family: American Typewriter;
    font-size: 36px;
    margin-right: 56px;
`;

const Caption = styled.div`
    font-family: American Typewriter;
`;

const Body = styled.div`
    padding: 16px;
`;

const Links = styled.div`
    padding-bottom: 16px;
`;

const DiscDB = (): JSX.Element => (
    <>
        <Header>
            <Title>DiscDB</Title>
            <Caption>A database of CD metadata</Caption>
        </Header>
        <Body>
            <Links>
                <Link to="/">Home</Link> | <Link to="/api">API</Link> |{" "}
                <Link to="/search">Search</Link> | <Link to="/contact">Contact</Link>
            </Links>

            <Routes>
                <Route path="/api" element={<API />} />
                <Route path="/search" element={<Search />} />
                <Route path="/contact" element={<Contact />} />
                <Route path="/" element={<Home />} />
            </Routes>
        </Body>
    </>
);

export default DiscDB;
