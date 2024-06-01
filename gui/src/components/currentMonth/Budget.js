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
                <Table responsive='sm' striped bordered size="sm">
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
                        <td>{row.currentMonthSum}</td>
                        <td>{row.difference}</td>
                    </tr>)}
                    </tbody>
                </Table>
            </Col>
            <Col sm={5}>
                <h5>NO BUY</h5>
                <Table responsive='sm' striped bordered size="sm">
                    <tbody>
                    <tr className='table-info'>
                        <td>KATEGORIA</td>
                        <td>SUMA</td>
                    </tr>
                    {budgetPlan.filter(g => g.goal === 0).map(row => <tr key={row.id}>
                        <td>{row.category}</td>
                        <td>{row.currentMonthSum}</td>
                    </tr>)}
                    </tbody>
                </Table>
            </Col>
        </>);
}
