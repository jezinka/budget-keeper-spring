import React, {useEffect, useState} from 'react';
import Table from "react-bootstrap/Table";
import {Col} from "react-bootstrap";

export default function Budget() {
    const [budgetPlan, setBudgetPlan] = useState([]);

    async function loadData() {
        const response = await fetch('/budget/expenses/budgetPlan')
        const data = await response.json();
        setBudgetPlan(data);
    }

    useEffect(() => {
        loadData();
    }, []);

    return (
        <>
            <Col sm={4}>
                <Table responsive='sm' striped bordered size="sm">
                    <tbody>
                    <tr className='table-info'>
                        <td>KATEGORIA</td>
                        <td>SUMA</td>
                        <td>ŚREDNIA</td>
                        <td>ZAŁOŻENIE</td>
                    </tr>
                    {budgetPlan.map(row => <tr key={row.id}>
                        <td>{row.category}</td>
                        <td>{row.currentMonthSum}</td>
                        <td>{row.previousYearMean}</td>
                        <td>{row.goal}</td>
                    </tr>)}
                    </tbody>
                </Table>
            </Col>
        </>);
}
