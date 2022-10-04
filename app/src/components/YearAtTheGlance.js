import {Col, Container, Row} from "react-bootstrap";
import ErrorLog from "./ErrorLog";
import React from "react";
import YearlyExpenses from "./YearlyExpenses";

const YearAtTheGlance = () => {
    return (
        <Container fluid className="mt-2">
            <Row>
                <ErrorLog/>
            </Row>
            <Row/>
            <Row>
                <Col sm={1}></Col>
                <Col><YearlyExpenses/></Col>
                <Col sm={1}></Col>
            </Row>
        </Container>
    )
}

export default YearAtTheGlance;