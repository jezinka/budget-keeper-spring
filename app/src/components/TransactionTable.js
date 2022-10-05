import Table from 'react-bootstrap/Table';
import {useEffect, useState} from "react";
import {Button, Form, Modal} from "react-bootstrap";
import {ArrowsAngleExpand, Pencil, Trash} from "react-bootstrap-icons";

export default function TransactionTable() {
    const [transactions, setTransactions] = useState([]);
    const [showForm, setShowForm] = useState(false);

    const [categories, setCategories] = useState([]);

    const [formState, setFormState] = useState({
        "id": 0,
        "transactionDate": Date.now(),
        "title": "",
        "payee": "",
        "amount": 0,
        "categoryId": -1
    })

    const handleChange = (event) => {
        setFormState({...formState, [event.target.name]: event.target.value});
    };

    async function reloadTable() {
        const response = await fetch('/transactions');
        const data = await response.json();
        setTransactions(data);
        setShowForm(false);
    }

    async function fetchCategories() {
        const response = await fetch('/categories');
        const data = await response.json();
        setCategories(data);
    }

    useEffect(() => {
        reloadTable();
        fetchCategories();
    }, []);

    async function deleteTransaction(id) {
        const success = await fetch("/transactions/" + id, {method: 'DELETE'})
        const data = await success.json();
        if (data) {
            reloadTable();
        }
    }

    async function editTransaction(id) {
        const transaction = await fetch("/transactions/" + id, {method: 'GET'})
        const data = await transaction.json();

        setFormState({
            "id": id,
            "transactionDate": data.transactionDate,
            "title": data.title,
            "payee": data.payee,
            "amount": data.amount,
            "categoryId": data.categoryId ? data.categoryId : -1
        });
        setShowForm(true);
    }

    const handleClose = () => setShowForm(false);

    async function submitForm() {
        const response = await fetch('/transactions/' + formState.id, {
            method: 'PUT',
            body: JSON.stringify(formState),
            headers: {'Content-Type': 'application/json'},
        });
        const data = await response.json();
        if (data) {
            reloadTable();
        }
    }

    return (
        <>
            <Modal show={showForm} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Edytuj transakcję</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-3">
                            <Form.Label>Kiedy:</Form.Label>
                            <Form.Control type="date" onChange={handleChange} name="transactionDate"
                                          value={formState.transactionDate}/>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Co:</Form.Label>
                            <Form.Control type="text" onChange={handleChange} name="title" value={formState.title}/>
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Kto:</Form.Label>
                            <Form.Control type="text" onChange={handleChange} name="payee" value={formState.payee}/>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Ile:</Form.Label>
                            <Form.Control type="number" onChange={handleChange} name="amount" value={formState.amount}/>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Kategoria:</Form.Label>
                            <Form.Select onChange={handleChange} name="categoryId" value={formState.categoryId}>
                                <option value={-1}></option>
                                {categories.map(c =>
                                    <option key={c.id} value={c.id}>{c.name}</option>
                                )}
                            </Form.Select>
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
                {transactions.map(transaction =>
                    <tr key={transaction.id}>
                        <td>{transaction.transactionDate}</td>
                        <td>{transaction.title}</td>
                        <td>{transaction.payee}</td>
                        <td>{transaction.amount}</td>
                        <td>{transaction.category}</td>
                        <td>
                            <Button variant="outline-primary" size="sm"
                                    value={transaction.id}
                                    onClick={(e) => editTransaction(e.currentTarget.value)}><Pencil/>
                            </Button>{' '}
                            <Button variant="outline-primary" size="sm"><ArrowsAngleExpand/></Button>{' '}
                            <Button variant="outline-primary" size="sm"
                                    value={transaction.id}
                                    onClick={(e) => deleteTransaction(e.currentTarget.value)}><Trash/>
                            </Button>
                        </td>
                    </tr>
                )}
                </tbody>
            </Table>
        </>
    );
}