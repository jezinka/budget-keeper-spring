import Table from 'react-bootstrap/Table';
import React, {useEffect, useState} from "react";
import {Button, Col, Form, Modal, Row, Spinner} from "react-bootstrap";
import {ArrowClockwise, ArrowsAngleExpand, Pencil, Trash} from "react-bootstrap-icons";
import {EMPTY_OPTION, handleError} from "../../Utils";

export default function TransactionTable({mode, counterHandler, filterForm}) {
    const [transactions, setTransactions] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const [showSpinner, setShowSpinner] = useState(false);
    const [splitFlow, setSplitFlow] = useState(false);
    const [categories, setCategories] = useState([]);
    const [liabilities, setLiabilities] = useState([]);
    const [formState, setFormState] = useState({
        "id": 0,
        "transactionDate": Date.now(),
        "title": "",
        "payee": "",
        "baseSplitAmount": 0,
        "amount": 0,
        "categoryId": EMPTY_OPTION,
        "liabilityId": EMPTY_OPTION,
        "splitAmount": 0,
        "splitCategoryId": EMPTY_OPTION,
        "splitLiabilityId": EMPTY_OPTION,
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
        setShowSpinner(true);
        const response = await getResponse();
        setShowForm(false);
        setSplitFlow(false);
        setShowSpinner(false);
        if (response.ok) {
            const data = await response.json();
            if (data) {
                if (counterHandler !== undefined) {
                    counterHandler(data.length);
                }
                return setTransactions(data);
            }
        }
        return handleError();
    }

    async function getResponse() {
        if (mode === "currentMonth") {
            return await fetch("/transactions/currentMonth");
        } else {
            return await fetch('/transactions', {
                method: "POST", body: JSON.stringify(filterForm), headers: {'Content-Type': 'application/json'},
            });
        }
    }

    async function fetchCategories() {
        const response = await fetch('/categories');
        if (response.ok) {
            const data = await response.json();
            if (data) {
                return setCategories(data);
            }
        }
        return handleError();
    }

    async function fetchLiabilities() {
        const response = await fetch('/liabilities');
        if (response.ok) {
            const data = await response.json();
            if (data) {
                return setLiabilities(data);
            }
        }
        return handleError();
    }

    useEffect(() => {
        reloadTable();
        fetchCategories();
        fetchLiabilities();
    }, []);

    async function deleteTransaction(id) {
        const response = await fetch("/transactions/" + id, {method: 'DELETE'})
        return await handleResponse(response);
    }

    async function editTransaction(id) {
        const transaction = await fetch("/transactions/" + id, {method: 'GET'})
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
                    "categoryId": data.categoryId ? data.categoryId : EMPTY_OPTION,
                    "liabilityId": data.liabilityId ? data.liabilityId : EMPTY_OPTION
                });
                return setShowForm(true);
            }
        }
        return handleError();
    }

    async function splitTransaction(id) {
        setSplitFlow(true);
        editTransaction(id);
    }

    const handleClose = () => {
        setShowForm(false);
        setSplitFlow(false);
    }

    async function submitForm() {
        const response = await fetch('/transactions/' + formState.id, {
            method: 'PUT', body: JSON.stringify(formState), headers: {'Content-Type': 'application/json'},
        });

        return await handleResponse(response)
    }

    async function submitSplitForm() {
        const newTransaction = {
            ...formState,
            amount: formState.splitAmount,
            categoryId: formState.splitCategoryId,
            liabilityId: formState.splitLiabilityId
        };

        const response = await fetch('/transactions/split/' + formState.id, {
            method: 'POST',
            body: JSON.stringify([formState, newTransaction]),
            headers: {'Content-Type': 'application/json'},
        });

        return await handleResponse(response);
    }

    function getCategoriesMap() {
        let categoriesList = [<option key={EMPTY_OPTION} value={EMPTY_OPTION}></option>];
        categories.forEach((c) => {
            categoriesList.push(<option key={c.id} value={c.id}>{c.name}</option>)
        });
        return categoriesList
    }

    function getLiabilitiesMap() {
        let liabilitiesList = [<option key={EMPTY_OPTION} value={EMPTY_OPTION}></option>];
        liabilities.forEach((l) => {
            liabilitiesList.push(<option key={l.id} value={l.id}>{l.name} ({l.bank})</option>)
        });
        return liabilitiesList
    }

    async function handleResponse(response) {
        if (response.ok) {
            const data = await response.json();
            if (data) {
                return reloadTable();
            }
        }
        return handleError();
    }

    function refreshButton() {
        if (mode === 'all') {
            return <Col sm={1}>
                <Button onClick={reloadTable}>{showSpinner ? <Spinner size={"sm"} animation="grow"/> :
                    <ArrowClockwise/>}</Button>
            </Col>;
        }
    }

    return (<>
        {refreshButton()}
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
                    <Row>
                        <Col sm={3}>
                            {splitFlow ?
                                <Form.Control className="m-2" placeholder="Ile:" type="number" onChange={handleChange}
                                              name="amount" value={formState.amount}/> : ''}
                        </Col>
                        <Col sm={4}>
                            <Form.Select className="m-2" placeholder="Kategoria:" onChange={handleChange}
                                         name="categoryId" value={formState.categoryId}>
                                {getCategoriesMap()}
                            </Form.Select>
                        </Col>
                        <Col sm={4}>
                            <Form.Select className="m-2" placeholder="Pasywa:" onChange={handleChange}
                                         name="liabilityId" value={formState.liabilityId}>
                                {getLiabilitiesMap()}
                            </Form.Select>
                        </Col>
                    </Row>
                    {splitFlow ? <Row>
                        <Col sm={3}>
                            <Form.Control className="m-2" placeholder="Ile:" type="number"
                                          onChange={handleSplit}
                                          name="splitAmount" value={formState.splitAmount}/>
                        </Col>
                        <Col sm={4}>
                            <Form.Select className="m-2" placeholder="Kategoria:" onChange={handleChange}
                                         name="splitCategoryId" value={formState.splitCategoryId}>
                                {getCategoriesMap()}
                            </Form.Select>
                        </Col>
                        <Col sm={4}>
                            <Form.Select className="m-2" placeholder="Pasywa:" onChange={handleChange}
                                         name="splitLiabilityId" value={formState.splitLiabilityId}>
                                {getLiabilitiesMap()}
                            </Form.Select>
                        </Col>
                    </Row> : ''}
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={handleClose}>
                    Zamknij
                </Button>
                {splitFlow ? '' : <Button variant="primary" onClick={submitForm}> Zapisz </Button>}
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
            {transactions.map(transaction => <tr key={transaction.id}>
                <td>{transaction.transactionDate}</td>
                <td>{transaction.title}</td>
                <td>{transaction.payee}</td>
                <td>{transaction.amount}</td>
                <td>{transaction.category}</td>
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
            </tr>)}
            </tbody>
        </Table>
    </>);
}