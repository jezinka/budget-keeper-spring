import React, {useEffect, useState} from "react";
import {Button, Form, Modal, Table} from "react-bootstrap";
import {PencilSquare, Trash} from "react-bootstrap-icons";
import {formatNumber} from "../../Utils";

const RecurringPaymentsAdmin = () => {
    const [payments, setPayments] = useState([]);
    const [editingPayment, setEditingPayment] = useState(null);

    async function loadPayments() {
        const response = await fetch("/budget/recurringPlannedExpenses");
        if (response.ok) setPayments(await response.json());
    }

    useEffect(() => {
        loadPayments();
    }, []);

    async function savePayment() {
        const response = await fetch(`/budget/recurringPlannedExpenses/${editingPayment.id}`, {
            method: "PUT",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({...editingPayment, amount: Number(editingPayment.amount)})
        });
        if (!response.ok) return alert(await response.text());
        setEditingPayment(null);
        loadPayments();
    }

    async function deletePayment(id) {
        if (!window.confirm("Usunąć płatność cykliczną?")) return;
        const response = await fetch(`/budget/recurringPlannedExpenses/${id}`, {method: "DELETE"});
        if (!response.ok) return alert(await response.text());
        loadPayments();
    }

    return <>
        <Table striped hover size="sm">
            <thead><tr><th>Nazwa</th><th>Kwota</th><th>Dzień</th><th>Aktywna</th><th/></tr></thead>
            <tbody>{payments.map(payment => <tr key={payment.id}>
                <td>{payment.name}</td><td>{formatNumber(payment.amount)}</td><td>{payment.dueDay}.</td>
                <td>{payment.active ? "✓" : "✗"}</td>
                <td className="text-end"><Button size="sm" variant="outline-secondary" className="me-1"
                    onClick={() => setEditingPayment({...payment})}><PencilSquare/></Button>
                    <Button size="sm" variant="outline-danger" onClick={() => deletePayment(payment.id)}><Trash/></Button></td>
            </tr>)}</tbody>
        </Table>
        <Modal show={editingPayment !== null} onHide={() => setEditingPayment(null)}>
            <Modal.Header closeButton><Modal.Title>Edytuj płatność cykliczną</Modal.Title></Modal.Header>
            <Modal.Body>
                <Form.Group className="mb-3"><Form.Label>Nazwa</Form.Label>
                    <Form.Control value={editingPayment?.name || ""} onChange={e => setEditingPayment(
                        {...editingPayment, name: e.target.value})}/></Form.Group>
                <Form.Group className="mb-3"><Form.Label>Kwota</Form.Label>
                    <Form.Control min="0.01" step="0.01" type="number" value={editingPayment?.amount || ""}
                        onChange={e => setEditingPayment({...editingPayment, amount: e.target.value})}/></Form.Group>
                <Form.Group className="mb-3"><Form.Label>Dzień miesiąca</Form.Label>
                    <Form.Control min="1" max="31" type="number" value={editingPayment?.dueDay || ""}
                        onChange={e => setEditingPayment({...editingPayment, dueDay: Number(e.target.value)})}/></Form.Group>
                <Form.Check type="switch" label="Aktywna" checked={editingPayment?.active || false}
                    onChange={e => setEditingPayment({...editingPayment, active: e.target.checked})}/>
            </Modal.Body>
            <Modal.Footer><Button variant="secondary" onClick={() => setEditingPayment(null)}>Anuluj</Button>
                <Button onClick={savePayment}>Zapisz</Button></Modal.Footer>
        </Modal>
    </>;
};

export default RecurringPaymentsAdmin;
