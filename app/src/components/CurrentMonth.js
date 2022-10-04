import {Col, Container, Row} from "react-bootstrap";
import ErrorLog from "./ErrorLog";
import TransactionTable from "./TransactionTable";
import MoneyAmount from "./MoneyAmount";
import React from "react";

const CurrentMonth = () => {
    return (
        <Container fluid className="mt-2">
            <Row>
                <ErrorLog/>
            </Row>
            <Row/>
            <Row>
                <Col sm={7}><TransactionTable/></Col>
                <Col sm={2} className="mt-4">
                    <MoneyAmount/>
                </Col>
            </Row>
        </Container>
    );
}

export default CurrentMonth;