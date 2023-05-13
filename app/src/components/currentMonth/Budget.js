import React, {useEffect, useState} from 'react';
import Table from "react-bootstrap/Table";
import {Col, Row} from "react-bootstrap";

export default function Budget() {
    const [budgetPlan, setBudgetPlan] = useState([]);

    async function loadData() {
        const response = await fetch('/expenses/budgetPlan')
        const data = await response.json();
        setBudgetPlan(data);
    }

    useEffect(() => {
        loadData();
    }, []);

    return (
        <>
            <Row>
                <Col sm={10}>
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
            </Row>
        </>);
}
