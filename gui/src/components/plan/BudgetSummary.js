import React, {useEffect, useState} from 'react';
import Table from "react-bootstrap/Table";
import {Col, Row} from "react-bootstrap";
import {formatNumber} from "../../Utils";

export default function BudgetSummary() {
    const [budgetPlanSummary, setBudgetPlanSummary] = useState({});
    const [moneyAmount, setMoneyAmount] = useState({});

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

    useEffect(() => {
        loadData();
        moneyAmountLoad();
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
