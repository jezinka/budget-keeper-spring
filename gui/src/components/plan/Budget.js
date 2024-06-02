import React, {useEffect, useState} from 'react';
import Table from "react-bootstrap/Table";
import {Col} from "react-bootstrap";

export default function Budget() {
    const [budgetPlan, setBudgetPlan] = useState([]);

    async function loadData() {
        const response = await fetch('/budget/budgetPlan')
        const data = await response.json();
        setBudgetPlan(data);
    }

    useEffect(() => {
        loadData();
    }, []);

    return (
        <>
            <Col sm={5}>
                <h5>PLAN</h5>
                <Table id='budgetPlanTable' responsive='sm' striped bordered size="sm">
                    <tbody>
                    <tr className='table-info'>
                        <td>KATEGORIA</td>
                        <td>ZAŁOŻENIE</td>
                        <td>SUMA</td>
                        <td>RÓŻNICA</td>
                    </tr>
                    {budgetPlan.filter(g => g.goal !== 0).map(row => <tr key={row.id}>
                        <td>{row.category}</td>
                        <td>{row.goal}</td>
                        <td>{row.expense}</td>
                        <td className={row.percentage > 105 ? 'failed_goal' : 'success_goal'}>{row.difference}</td>
                    </tr>)}
                    <tr>
                        <td>RAZEM</td>
                        <td>{budgetPlan.filter(g => g.goal !== 0).map(row => row.goal).reduce((sum, num) => sum + num, 0)}</td>
                        <td>{budgetPlan.filter(g => g.goal !== 0).map(row => row.expense).reduce((sum, num) => sum + num, 0)}</td>
                        <td>{budgetPlan.filter(g => g.goal !== 0).map(row => row.difference).reduce((sum, num) => sum + num, 0)}</td>
                    </tr>
                    </tbody>
                </Table>
            </Col>
            <Col sm={5}>
                <h5>NO BUY</h5>
                <Table id='noBuyTable' responsive='sm' striped bordered size="sm">
                    <tbody>
                    <tr className='table-info'>
                        <td>KATEGORIA</td>
                        <td>SUMA</td>
                    </tr>
                    {budgetPlan.filter(g => g.goal === 0).map(row => <tr key={row.id}>
                        <td>{row.category}</td>
                        <td className={row.expense === 0 ? 'success_goal' : 'failed_goal'}>{row.expense}</td>
                    </tr>)}
                    <tr>
                        <td>RAZEM</td>
                        <td>{budgetPlan.filter(g => g.goal === 0).map(row => row.expense).reduce((sum, num) => sum + num, 0)}</td>
                        </tr>
                    </tbody>
                </Table>
            </Col>
        </>);
}
