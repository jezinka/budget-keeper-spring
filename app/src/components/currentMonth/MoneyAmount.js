import React, {useEffect, useState} from 'react';
import Table from "react-bootstrap/Table";
import {formatNumber, handleError} from "../../Utils";
import {Plus} from "react-bootstrap-icons";
import {Button, Col, Form, Modal, Row} from "react-bootstrap";

export default function MoneyAmount() {
    const [moneyAmount, setMoneyAmount] = useState({});
    const [showForm, setShowForm] = useState(false);
    const [formState, setFormState] = useState({"amount": 0})

    async function submitForm() {
        const response = await fetch('/moneyAmount/', {
            method: 'PUT',
            body: JSON.stringify(formState),
            headers: {'Content-Type': 'application/json'},
        });
        if (response.ok) {
            const data = await response.json();
            if (data) {
                setShowForm(false);
                setFormState({"amount": 0});
                return await loadData();
            }
        }
        handleError();
    }

    const handleClose = () => setShowForm(false);

    const handleChange = (event) => {
        setFormState({...formState, [event.target.name]: event.target.value});
    };

    async function loadData() {
        const response = await fetch('/moneyAmount')
        const data = await response.json();
        setMoneyAmount(data);
    }

    useEffect(() => {
        loadData();
    }, []);

    return (
        <>
            <Modal show={showForm} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Stan konta na początku miesiąca</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-3">
                            <Form.Label>Ile:</Form.Label>
                            <Form.Control type="number" onChange={handleChange} name="amount"
                                          value={formState.amount}
                                          autoFocus/>
                        </Form.Group>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}>
                        Zamknij
                    </Button>
                    <Button variant="primary" onClick={submitForm}>
                        Zapisz
                    </Button>
                </Modal.Footer>
            </Modal>
            <Row>
                <Col sm={10}>
                    <Table responsive='sm' striped bordered size="sm">
                        <tbody>
                        <tr>
                            <td>NA WEJŚCIU</td>
                            <td>{formatNumber(moneyAmount.start)}</td>
                        </tr>
                        <tr>
                            <td>WYDATKI</td>
                            <td>{formatNumber(moneyAmount.expenses)}</td>
                        </tr>
                        <tr>
                            <td>WPŁYWY</td>
                            <td>{formatNumber(moneyAmount.incomes)}</td>
                        </tr>
                        <tr>
                            <td>STAN KONTA</td>
                            <td>{formatNumber(moneyAmount.accountBalance)}</td>
                        </tr>
                        </tbody>
                    </Table>
                </Col>
                <Col sm={1}>
                    <Button size={"sm"} onClick={() => {
                        setShowForm(true)
                    }}>
                        <Plus/>
                    </Button>
                </Col>
            </Row>
        </>);
}
