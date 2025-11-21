import React, {useEffect, useState} from 'react';
import Table from "react-bootstrap/Table";
import {Col, Row} from "react-bootstrap";
import {formatNumber} from "../../Utils";

export default function BudgetSummary() {
    const [budgetPlanSummary, setBudgetPlanSummary] = useState({});
    const [moneyAmount, setMoneyAmount] = useState({});
    const [overSum, setOverSum] = useState(0);

    async function loadData() {
        const response = await fetch('/budget/budgetPlan/summary')
        const data = await response.json();
        setBudgetPlanSummary(data);
    }

    async function moneyAmountLoad() {
        const response = await fetch('/budget/moneyAmount')
        const data = await response.json();
        setMoneyAmount(data);
    }

    async function overLoad() {
        try {
            // pobierz wydatki dla obecnego miesiąca
            const now = new Date();
            const year = now.getFullYear();
            const month = now.getMonth() + 1; // API oczekuje 1-12
            const expensesResp = await fetch(`/budget/expenses/selectedMonth?year=${year}&month=${month}`);
            const expenses = await expensesResp.json();

            // filtruj wydatki o poziomie 'ponad' i policz sumę (wartość dodatnia)
            const ponadExpenses = expenses.filter(e => e.categoryLevel !== null && e.categoryLevel !== undefined && parseInt(e.categoryLevel) === 3);
            const sum = ponadExpenses.reduce((acc, e) => acc + Math.abs(Number(e.amount)), 0);
            setOverSum(sum);
        } catch (err) {
            setOverSum(0);
        }
    }

    useEffect(() => {
        loadData();
        moneyAmountLoad();
        overLoad();
    }, []);

    return (
        <>
            <Row>
                <Col sm={6}>
                    <Table responsive='sm' striped bordered size="sm">
                        <tbody>
                        <tr>
                            <td className='table-info'>NA WEJŚCIU</td>
                            <td>{formatNumber(moneyAmount.start)}</td>
                        </tr>
                        <tr>
                            <td className='table-info'>WPŁYWY</td>
                            <td>{formatNumber(moneyAmount.incomes)}</td>
                        </tr>
                        <tr>
                            <td className='table-info'>WSZYSTKIE</td>
                            <td>{formatNumber(moneyAmount.expenses)}</td>
                        </tr>
                        <tr>
                            <td className='table-info'>STAN KONTA</td>
                            <td>{formatNumber(moneyAmount.accountBalance)}</td>
                        </tr>
                        </tbody>
                    </Table>
                </Col>
                <Col sm={6} className={"mt-4"}>
                    <Table responsive='sm' striped bordered size="sm">
                        <tbody>
                        <tr>
                            <td className='table-info'>PONAD</td>
                            <td>{formatNumber(overSum)}</td>
                        </tr>
                        <tr>
                            <td className='table-info'>INWESTYCJE</td>
                            <td>{formatNumber(budgetPlanSummary.investments)}</td>
                        </tr>
                        <tr>
                            <td className='table-info'>WOLNE ŚRODKI</td>
                            <td>{formatNumber(moneyAmount.accountBalance + budgetPlanSummary.noPayGoal)}</td>
                        </tr>
                        </tbody>
                    </Table>
                </Col>
                <Col sm={2}/>
            </Row>
        </>);
}
