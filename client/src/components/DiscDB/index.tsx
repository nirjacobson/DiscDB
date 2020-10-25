import React from "react";
import styled from "@emotion/styled";
import Home from "components/Home";
import {BrowserRouter as Router, Switch, Route, Link} from "react-router-dom";

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
            <Caption>A free database of audio disc metadata</Caption>
        </Header>
        <Body>
            <Router>
                <Links>
                    <Link to="/">Home</Link>
                </Links>

                <Switch>
                    <Route path="/">
                        <Home />
                    </Route>
                </Switch>
            </Router>
        </Body>
    </>
);

export default DiscDB;
