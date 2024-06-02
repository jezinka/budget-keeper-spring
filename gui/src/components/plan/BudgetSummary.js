import React, {useEffect, useState} from 'react';
import Table from "react-bootstrap/Table";
import {Col} from "react-bootstrap";

export default function BudgetSummary() {
    const [budgetPlanSummary, setBudgetPlanSummary] = useState({});

    async function loadData() {
        const response = await fetch('/budget/budgetPlan/summary')
        const data = await response.json();
        setBudgetPlanSummary(data);
    }

    useEffect(() => {
        loadData();
    }, []);

    return (
        <>
            <Col>
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
        </>);
}
