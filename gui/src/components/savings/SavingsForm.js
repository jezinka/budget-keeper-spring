import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import React, {useState} from "react";

const SavingsForm = (props) => {

    const [savingsReadFormState, setSavingsReadFormState] = useState({date: new Date().toISOString().split('T')[0]})

    const submitForm = async () => {
        await fetch('/budget/savingsRead/', {
            method: 'POST', body: JSON.stringify({savingsReadFormState}), headers: {'Content-Type': 'application/json'},
        });
        props.close();
        setSavingsReadFormState({date: new Date().toISOString().split('T')[0]});
    };

    const handleChange = (event) => {
        setSavingsReadFormState({...savingsReadFormState, [event.target.name]: event.target.value});
    };

    return (<Modal show={props.show} onHide={props.close}>
        <Modal.Header closeButton>
            <Modal.Title>Oszczędności</Modal.Title>
        </Modal.Header>
        <Modal.Body>
            <Form>
                <Form.Group className="mb-3">
                    <Form.Label>Kiedy:</Form.Label>
                    <Form.Control type="date" onChange={handleChange} name="date"
                                  value={savingsReadFormState['date']}/>
                </Form.Group>
                <Form.Group className="mb-3" as={Row}>
                    {props.savingsList.map(savings => {
                        return <>
                            <Form.Label column sm={5}>{savings.fundGroup + ' ' + savings.name}</Form.Label>
                            <Col sm={7}>
                                <Form.Control
                                    type="number"
                                    onChange={handleChange}
                                    name={savings.savingsId}
                                    value={savingsReadFormState[savings.savingsId]}/>
                            </Col>
                        </>
                    })}
                </Form.Group>
            </Form>
        </Modal.Body>
        <Modal.Footer>
            <Button variant="secondary" onClick={props.close}>
                Zamknij
            </Button>
            <Button variant="primary" onClick={submitForm}>
                Zapisz
            </Button>
        </Modal.Footer>
    </Modal>)
}
export default SavingsForm;