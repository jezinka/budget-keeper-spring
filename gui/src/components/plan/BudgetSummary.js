import React, {useEffect, useState} from 'react';
import Table from "react-bootstrap/Table";
import {Col} from "react-bootstrap";
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
            <Col>

                <Table responsive='sm' striped bordered size="sm">
                    <tbody>
                    <tr>
                        <td className='table-info'>NA WEJŚCIU</td>
                        <td>{moneyAmount.start}</td>
                    </tr>
                    <tr>
                        <td className='table-info'>PO ZAPLANOWANYCH</td>
                        <td>{formatNumber(moneyAmount.start + budgetPlanSummary.sumGoal)}</td>
                    </tr>

                    </tbody>
                </Table>

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

                <Table responsive='sm' striped bordered size="sm">
                    <tbody>
                    <tr>
                        <td className='table-info'>WOLNE ŚRODKI</td>
                        <td>{formatNumber(moneyAmount.start +
                            budgetPlanSummary.sumGoal +
                            budgetPlanSummary.otherExpenses +
                            budgetPlanSummary.noBuy +
                            budgetPlanSummary.overGoalDifference)}</td>
                    </tr>
                    </tbody>
                </Table>
            </Col>
        </>);
}
