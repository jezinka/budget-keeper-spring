import Table from 'react-bootstrap/Table';
import React, {useEffect, useState} from "react";
import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import {ArrowsAngleExpand, Pencil, Plus, Trash} from "react-bootstrap-icons";
import {EMPTY_OPTION, formatNumber, handleError, UNKNOWN_CATEGORY} from "../../Utils";
import AddCategoryModal from "./AddCategoryModal";

export default function TransactionTable(props) {
    const [showForm, setShowForm] = useState(false);
    const [showCategoryForm, setShowCategoryForm] = useState(false);
    const [splitFlow, setSplitFlow] = useState(false);
    const [categories, setCategories] = useState([]);
    const [formState, setFormState] = useState({
        "id": 0,
        "transactionDate": Date.now(),
        "title": "",
        "payee": "",
        "baseSplitAmount": 0,
        "amount": 0,
        "categoryId": EMPTY_OPTION,
        "splitAmount": 0,
        "splitCategoryId": EMPTY_OPTION
    })

    const handleChange = (event) => {
        setFormState({...formState, [event.target.name]: event.target.value});
    };

    const handleSplit = (event) => {
        let value = Number(event.target.value);
        let newValue = Number((formState.baseSplitAmount - value).toFixed(2))
        setFormState({...formState, [event.target.name]: value, amount: newValue});
    };

    async function reloadTable() {
        setShowForm(false);
        setSplitFlow(false);
        props.changeTransactionsHandler();
    }

    async function fetchCategories() {
        const response = await fetch('/budget/categories/all');
        if (response.ok) {
            const data = await response.json();
            if (data) {
                return setCategories(data);
            }
        }
        return handleError();
    }

    useEffect(() => {
        fetchCategories();
    }, []);

    async function deleteTransaction(id) {
        const response = await fetch('/budget/expenses/' + id, {method: 'DELETE'})
        if (response.ok) {
            return reloadTable();
        }
        return handleError();
    }

    async function editTransaction(id) {
        const transaction = await fetch('/budget/expenses/' + id, {method: 'GET'})
        if (transaction.ok) {
            const data = await transaction.json();
            if (data) {
                setFormState({
                    "id": id,
                    "transactionDate": data.transactionDate,
                    "title": data.title,
                    "payee": data.payee,
                    "amount": data.amount,
                    "baseSplitAmount": data.amount,
                    "categoryId": data.categoryId ? data.categoryId : EMPTY_OPTION
                });
                return setShowForm(true);
            }
        }
        return handleError();
    }

    async function splitTransaction(id) {
        setSplitFlow(true);
        await editTransaction(id);
    }

    const handleClose = () => {
        setShowForm(false);
        setSplitFlow(false);
    }

    async function submitForm() {
        const response = await fetch('/budget/expenses/' + formState.id, {
            method: 'PUT', body: JSON.stringify(formState), headers: {'Content-Type': 'application/json'},
        });

        if (response.ok) {
            return reloadTable();
        }
        return handleError();
    }

    async function submitSplitForm() {
        const splittedTransactions = [
            {
                ...formState,
                id: null,
                amount: formState.splitAmount,
                categoryId: formState.splitCategoryId
            },
            {
                ...formState,
                id: null,
                categoryId: formState.categoryId
            }
        ]

        const response = await fetch('/budget/expenses/split/' + formState.id, {
            method: 'POST',
            body: JSON.stringify(splittedTransactions),
            headers: {'Content-Type': 'application/json'},
        });

        if (response.ok) {
            return reloadTable();
        }
        return handleError();
    }

    function getCategoriesMap() {
        let categoriesList = [<option key={EMPTY_OPTION} value={EMPTY_OPTION}></option>];
        categories.forEach((c) => {
            categoriesList.push(<option key={c.id} value={c.id}>{c.name}</option>)
        });
        return categoriesList
    }

    return (<>
            <AddCategoryModal show={showCategoryForm} close={() => {
                setShowCategoryForm(false);
                fetchCategories();
            }}/>

            <Modal size="lg" show={showForm} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Edytuj transakcję:</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Row>
                            <Col sm={3}>
                                <Form.Control className="m-2"
                                              placeholder="Kiedy:" type="date" disabled onChange={handleChange}
                                              name="transactionDate"
                                              value={formState.transactionDate}/>
                            </Col>
                            <Col sm={8}>
                                <Form.Control className="m-2"
                                              placeholder="Co:" type="text" disabled onChange={handleChange}
                                              name="title"
                                              value={formState.title}/>
                            </Col>
                            <Col sm={3}>
                                <Form.Control className="m-2" placeholder="Ile:" type="number" disabled
                                              name="baseSplitAmount" value={formState.baseSplitAmount}/>
                            </Col>
                            <Col sm={{span: 8}}>
                                <Form.Control className="m-2"
                                              placeholder="Kto:" type="text" disabled onChange={handleChange}
                                              name="payee"
                                              value={formState.payee}/>
                            </Col>
                        </Row>
                        <Row className="align-items-center">
                            <Col sm={3}>
                                {splitFlow ?
                                    <Form.Control className="m-2" placeholder="Ile:" type="number"
                                                  onChange={handleChange}
                                                  name="amount" value={formState.amount}/> : ''}
                            </Col>

                            <Col sm={6}>
                                <Form.Select className="m-2" placeholder="Kategoria:" onChange={handleChange}
                                             name="categoryId" value={formState.categoryId}
                                             autoFocus>
                                    {getCategoriesMap()}
                                </Form.Select>
                            </Col>

                            <Col sm={1}>
                                <Button size={"sm"} onClick={() => {
                                    setShowCategoryForm(true);
                                }}> <Plus/> </Button>
                            </Col>

                        </Row>
                        {splitFlow ? <Row>
                            <Col sm={3}>
                                <Form.Control className="m-2" placeholder="Ile:" type="number"
                                              onChange={handleSplit}
                                              name="splitAmount" value={formState.splitAmount}/>
                            </Col>
                            <Col sm={8}>
                                <Form.Select className="m-2" placeholder="Kategoria:" onChange={handleChange}
                                             name="splitCategoryId" value={formState.splitCategoryId}>
                                    {getCategoriesMap()}
                                </Form.Select>
                            </Col>
                        </Row> : ''}
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}> Zamknij </Button>
                    {splitFlow ? '' :
                        <Button variant="primary" onClick={submitForm}> Zapisz </Button>}
                    {splitFlow ? <Button variant="primary" onClick={submitSplitForm}> Rozdziel </Button> : ''}
                </Modal.Footer>
            </Modal>

            <Table responsive='sm' striped bordered size="sm">
                <thead>
                <tr className='table-info'>
                    <th>KIEDY</th>
                    <th>CO</th>
                    <th>KTO</th>
                    <th>ILE</th>
                    <th>KATEGORIA</th>
                    <th style={{textAlign: "center"}}>*</th>
                </tr>
                </thead>
                <tbody>
                {props.transactions.map(transaction => {
                    return <tr key={transaction.id}>
                        <td>{transaction.transactionDate}</td>
                        <td>{transaction.title.substring(0, 50)}</td>
                        <td>{transaction.payee.substring(0, 50)}</td>
                        <td style={{textAlign: 'right'}}>{formatNumber(transaction.amount)}</td>
                        <td style={{color: (transaction.categoryId === UNKNOWN_CATEGORY ? "lightgray" : "black")}}>{transaction.categoryName}</td>
                        <td style={{textAlign: "center"}}>
                            <Button variant="outline-primary" size="sm"
                                    onClick={() => editTransaction(transaction.id)}><Pencil/>
                            </Button>{' '}
                            <Button variant="outline-primary" size="sm"
                                    onClick={() => splitTransaction(transaction.id)}><ArrowsAngleExpand/>
                            </Button>{' '}
                            <Button variant="outline-primary" size="sm"
                                    onClick={() => deleteTransaction(transaction.id)}><Trash/>
                            </Button>
                        </td>
                    </tr>;
                })}
                </tbody>
            </Table>
        </>
    );
}