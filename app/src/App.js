import React from 'react';
import ErrorLog from './ErrorLog'
import './App.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'jquery/dist/jquery.min.js'
import 'bootstrap/dist/js/bootstrap.min.js'
import TransactionTable from "./TransactionTable";
import {Col, Container, Row} from "react-bootstrap";
import MoneyAmount from "./MoneyAmount";


const App = () => {
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

export default App;