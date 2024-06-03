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
                            <td>{moneyAmount.start}</td>
                        </tr>
                        <tr>
                            <td className='table-info'>WPŁYWY</td>
                            <td>{moneyAmount.incomes}</td>
                        </tr>
                        <tr>
                            <td className='table-info'>ZAPLANOWANE</td>
                            <td>{formatNumber(budgetPlanSummary.sumGoal)}</td>
                        </tr>
                        <tr>
                            <td className='table-info'>STAN KONTA</td>
                            <td>{formatNumber(moneyAmount.accountBalance)}</td>
                        </tr>
                        </tbody>
                    </Table>
                </Col>
                <Col sm={5}>
                    <Table id='budgetPlanTableSummary' responsive='sm' striped bordered size="sm">
                        <tbody>
                        <tr>
                            <td className='table-info'>PLANOWANE</td>
                            <td>{budgetPlanSummary.sumPlanned}</td>
                        </tr>
                        <tr>
                            <td className='table-info'>NO BUY</td>
                            <td>{budgetPlanSummary.noBuy}</td>
                        </tr>
                        <tr>
                            <td className='table-info'>NIEPLANOWANE</td>
                            <td>{budgetPlanSummary.otherExpenses}</td>
                        </tr>
                        <tr>
                            <td className='table-info'>WSZYSTKIE</td>
                            <td>{budgetPlanSummary.total}</td>
                        </tr>
                        </tbody>
                    </Table>
                </Col>
            </Row>
            <Row>
                <Col sm={2}/>
                <Col>
                    <Table responsive='sm' striped bordered size="sm">
                        <tbody>
                        <tr>
                            <td className='table-info'>WOLNE ŚRODKI</td>
                            <td>{formatNumber(moneyAmount.start +
                                moneyAmount.incomes +
                                budgetPlanSummary.sumGoal +
                                budgetPlanSummary.otherExpenses +
                                budgetPlanSummary.noBuy +
                                budgetPlanSummary.overGoalDifference
                            )}</td>
                        </tr>
                        </tbody>
                    </Table>
                </Col>
                <Col sm={2}/>
            </Row>
        </>)
        ;
}
