import {Container, Row} from "react-bootstrap";
import ErrorLog from "./ErrorLog";
import React from "react";

const Main = ({body}) => {
    return (
        <Container fluid className="mt-2">
            <Row>
                <ErrorLog/>
            </Row>
            <Row/>
            <Row>
                {body}
            </Row>
        </Container>
    );
}

export default Main;