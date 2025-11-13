import React, {useEffect, useState} from 'react';
import Table from "react-bootstrap/Table";
import {Button, Col, Modal, Row} from "react-bootstrap";
import {formatNumber} from "../../Utils";
import Expense from "../year/Expense";
import PlanImport from "./PlanImport";
import BudgetSummary from "./BudgetSummary";
import Main from "../main/Main";
import {Trash, Pencil} from "react-bootstrap-icons";

export default function Budget() {
    const [budgetPlan, setBudgetPlan] = useState([]);
    const [show, setShow] = useState(false);
    const [transactionsDetails, setTransactionsDetails] = useState([]);
    const [showEditModal, setShowEditModal] = useState(false);
    const [editGoalId, setEditGoalId] = useState(null);
    const [editAmount, setEditAmount] = useState('0');

    const [showUploadForm, setShowUploadForm] = useState(false);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    const openEditModal = (id, currentAmount) => {
        setEditGoalId(id);
        setEditAmount(currentAmount != null ? String(currentAmount) : '0');
        setShowEditModal(true);
    }

    const closeEditModal = () => {
        setShowEditModal(false);
        setEditGoalId(null);
        setEditAmount('0');
    }

    const saveEdit = async () => {
        if (editGoalId == null) return;
        try {
            const response = await fetch(`/budget/budgetPlan/${editGoalId}`, {
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({amount: editAmount})
            });
            if (response.ok) {
                closeEditModal();
                loadData();
            } else {
                const text = await response.text();
                alert('Aktualizacja nie powiodła się: ' + text);
            }
        } catch (e) {
            alert('Błąd sieci podczas aktualizacji: ' + e.message);
        }
    }

    async function loadData() {
        const response = await fetch('/budget/budgetPlan')
        const data = await response.json();
        setBudgetPlan(data);
    }

    const handleDeleteGoal = async (id) => {
        if (!window.confirm('Czy na pewno chcesz usunąć cel?')) return;
        try {
            const response = await fetch(`/budget/budgetPlan/${id}`, {method: 'DELETE'});
            if (response.ok) {
                loadData();
            } else {
                const text = await response.text();
                alert('Usuwanie nie powiodło się: ' + text);
            }
        } catch (e) {
            alert('Błąd sieci podczas usuwania: ' + e.message);
        }
    }

    const handleExport = async () => {
        const response = await fetch('/budget/budgetPlan/export', {
            method: 'GET',
        });

        if (response.ok) {
            const blob = await response.blob();
            const contentDisposition = response.headers.get('content-disposition');

            let filename = 'budgetPlan.csv';
            if (contentDisposition && contentDisposition.includes('filename=')) {
                filename = contentDisposition
                    .split('filename=')[1]
                    .split(';')[0]
                    .replace(/['"]/g, '');
            }

            const a = document.createElement('a');
            a.href = window.URL.createObjectURL(blob);
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            a.remove();
        } else {
            alert('Failed to export file.');
        }
    };


    useEffect(() => {
        loadData();
    }, []);

    function getGoalTotal() {
        return formatNumber(budgetPlan.filter(g => g.goal !== 0).map(row => row.goal).reduce((sum, num) => sum + num, 0));
    }

    function getExpenses() {
        return formatNumber(budgetPlan.filter(g => g.goal !== 0).map(row => row.expense).reduce((sum, num) => sum + num, 0));
    }


    function getTotalExpenses() {
        return formatNumber(budgetPlan.filter(g => g.goal !== 0).map(row => row.difference).reduce((sum, num) => sum + num, 0));
    }

    let currentMonth = new Date().getMonth() + 1;
    let currentYear = new Date().getFullYear();

    function getExpense(row) {
        return <Expense
            expense={{
                month: currentMonth,
                category: row.category,
                amount: row.expense,
                goalAmount: row.goal,
                transactionCount: row.transactionCount
            }}
            year={currentYear}
            key={currentMonth + row.category}
            modalHandler={handleShow}
            modalContentHandler={setTransactionsDetails}
            selectCurrentMonth={false}/>;
    }

    let body = <><>
            <Modal show={showUploadForm} onHide={() => setShowUploadForm(false)}>
                <Modal.Header closeButton> <Modal.Title>Załaduj</Modal.Title> </Modal.Header>
                <Modal.Body><PlanImport closeHandler={() => {
                    setShowUploadForm(false);
                    loadData();
                }}/></Modal.Body>
            </Modal>

            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton> <Modal.Title>Transakcje</Modal.Title> </Modal.Header>
                <Modal.Body>{transactionsDetails}</Modal.Body>
            </Modal>
            <Col sm={5}>
                <Row>
                    <Col sm={2}><h5>PLAN</h5></Col>
                    <Col sm={1} className={"mx-2"}> <Button size="sm" variant="info"
                                                            onClick={() => setShowUploadForm(true)}>Załaduj</Button></Col>
                    <Col sm={2} className={"mx-2"}> <Button size="sm" variant="secondary" onClick={async () => {
                        const response = await fetch('/budget/budgetPlan/autoFill', {method: 'POST'});
                        if (response.ok) {
                            loadData();
                        } else {
                            alert('Auto-fill failed');
                        }
                    }}>Auto-fill</Button></Col>
                    <Col sm={3} className={"mx-2"}> <Button size="sm" variant="outline-info" onClick={handleExport}>Zrzuć
                        Templatkę</Button></Col>
                </Row>
                <Table id='budgetPlanTable' responsive='sm' striped bordered size="sm">
                    <tbody>
                    <tr className='table-info'>
                        <td>KATEGORIA</td>
                        <td>ZAŁOŻENIE</td>
                        <td>WYDANE</td>
                        <td>RÓŻNICA</td>
                        <td>AKCJE</td>
                    </tr>
                    {budgetPlan.filter(g => g.goal !== 0).map(row => <tr key={row.id}>
                        <td>{row.category}</td>
                        <td>{formatNumber(row.goal)}</td>
                        {getExpense(row)}
                        <td> {formatNumber(row.difference)} </td>
                        <td>
                            <Button size="sm" variant="outline-primary" className="me-1"
                                    onClick={() => openEditModal(row.id, row.goal)}><Pencil/></Button>
                            <Button size="sm" variant="danger" onClick={() => handleDeleteGoal(row.id)}><Trash/></Button>
                        </td>
                    </tr>)}
                    <tr>
                        <td>RAZEM</td>
                        <td>{getGoalTotal()}</td>
                        <td>{getExpenses()}</td>
                        <td>{getTotalExpenses()}</td>
                        <td/>
                    </tr>
                    </tbody>
                </Table>

                {/* Modal edycji celu */}
                <Modal show={showEditModal} onHide={closeEditModal} centered>
                    <Modal.Header closeButton>
                        <Modal.Title>Edytuj cel</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <div className="mb-3">
                            <label className="form-label">Kwota</label>
                            <input
                                className="form-control"
                                type="number"
                                step="0.01"
                                value={editAmount}
                                onChange={e => setEditAmount(e.target.value)}
                            />
                        </div>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={closeEditModal}>Anuluj</Button>
                        <Button variant="primary" onClick={saveEdit}>Zapisz</Button>
                    </Modal.Footer>
                </Modal>
            </Col>
            <Col sm={3}>
                <h5>NO BUY</h5>
                <Table id='noBuyTable' responsive='sm' striped bordered size="sm">
                    <tbody>
                    <tr className='table-info'>
                        <td>KATEGORIA</td>
                        <td>WYDANE</td>
                        <td>AKCJE</td>
                    </tr>
                    {budgetPlan.filter(g => g.goal === 0).map(row => <tr key={row.id}>
                        <td>{row.category}</td>
                        {getExpense(row)}
                        <td>
                            <Button size="sm" variant="outline-primary" className="me-1"
                                    onClick={() => openEditModal(row.id, row.goal)}><Pencil/></Button>
                            <Button size="sm" variant="danger" onClick={() => handleDeleteGoal(row.id)}><Trash/></Button>
                        </td>
                    </tr>)}
                    <tr>
                        <td>RAZEM</td>
                        <td>{formatNumber(budgetPlan.filter(g => g.goal === 0).map(row => row.expense).reduce((sum, num) => sum + num, 0))}</td>
                        <td/>
                    </tr>
                    </tbody>
                </Table>
            </Col>
            <Col sm={3}> <BudgetSummary/></Col>
        </>
        </>
    ;
    return <Main body={body}/>;
}
