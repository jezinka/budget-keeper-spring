import {Col, Container, Row} from "react-bootstrap";
import ErrorLog from "./ErrorLog";
import React from "react";
import Navigation from "./Navigation";

const Main = ({body}) => {

    return (
        <Container fluid className="mt-2">
            <Row>
                <Col sm={1} className="pt-5" style={{borderRight: "2px solid #adb5bd"}}>
                    <Navigation/>
                </Col>
                <Col>
                    <Row>
                        <>
                            <ErrorLog/>
                            {body}
                        </>
                    </Row>
                </Col>
            </Row>
        </Container>
    );
}

export default Main;