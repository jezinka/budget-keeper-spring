import React, {useEffect, useState} from 'react';
import Table from "react-bootstrap/Table";
import {Col, Modal} from "react-bootstrap";
import {formatNumber} from "../../Utils";
import Expense from "../year/Expense";

export default function Budget() {
    const [budgetPlan, setBudgetPlan] = useState([]);
    const [show, setShow] = useState(false);
    const [transactionsDetails, setTransactionsDetails] = useState([]);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    async function loadData() {
        const response = await fetch('/budget/budgetPlan')
        const data = await response.json();
        setBudgetPlan(data);
    }

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
            expense={{month: currentMonth, category: row.category, amount: row.expense}}
            year={currentYear}
            key={currentMonth + row.category}
            modalHandler={handleShow}
            modalContentHandler={setTransactionsDetails}/>;
    }

    return (<>
        <Modal show={show} onHide={handleClose}>
            <Modal.Header closeButton> <Modal.Title>Transakcje</Modal.Title> </Modal.Header>
            <Modal.Body>{transactionsDetails}</Modal.Body>
        </Modal>
        <Col sm={5}>
            <h5>PLAN</h5>
            <Table id='budgetPlanTable' responsive='sm' striped bordered size="sm">
                <tbody>
                <tr className='table-info'>
                    <td>KATEGORIA</td>
                    <td>ZAŁOŻENIE</td>
                    <td>WYDANE</td>
                    <td>RÓŻNICA</td>
                </tr>
                {budgetPlan.filter(g => g.goal !== 0).map(row => <tr key={row.id}>
                    <td>{row.category}</td>
                    <td>{row.goal}</td>
                    <td>{getExpense(row)}</td>
                    <td className={row.percentage > 0 ? (row.percentage > 100 ? 'failed_goal' : 'over_goal') : 'success_goal'}>{row.difference}</td>
                </tr>)}
                <tr>
                    <td>RAZEM</td>
                    <td>{getGoalTotal()}</td>
                    <td>{getExpenses()}</td>
                    <td>{getTotalExpenses()}</td>
                </tr>
                </tbody>
            </Table>
        </Col>
        <Col sm={3}>
            <h5>NO BUY</h5>
            <Table id='noBuyTable' responsive='sm' striped bordered size="sm">
                <tbody>
                <tr className='table-info'>
                    <td>KATEGORIA</td>
                    <td>WYDANE</td>
                </tr>
                {budgetPlan.filter(g => g.goal === 0).map(row => <tr key={row.id}>
                    <td>{row.category}</td>
                    <td className={row.expense === 0 ? 'success_goal' : 'failed_goal'}>{getExpense(row)}</td>
                </tr>)}
                <tr>
                    <td>RAZEM</td>
                    <td>{formatNumber(budgetPlan.filter(g => g.goal === 0).map(row => row.expense).reduce((sum, num) => sum + num, 0))}</td>
                </tr>
                </tbody>
            </Table>
        </Col>
    </>);
}
