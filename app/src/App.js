import React from 'react';
import ErrorLog from './ErrorLog'
import './App.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'jquery/dist/jquery.min.js'
import 'bootstrap/dist/js/bootstrap.min.js'
import TransactionTable from "./TransactionTable";
import {Col, Container, Row} from "react-bootstrap";


const App = () => {
    return (
        <Container fluid>
            <Row>
                <ErrorLog/>
            </Row>
            <Row>
                <Col sm={7}><TransactionTable/></Col>
                <Col>Test</Col>
            </Row>
        </Container>
    );
}

export default App;