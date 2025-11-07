import {Col, Row} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import TransactionTableReadOnly from "../transactionTable/TransactionTableReadOnly";
import Table from "react-bootstrap/Table";
import {formatNumber} from "../../Utils";

const MonthlyView = () => {
    const [transactions, setTransactions] = useState([]);

    useEffect(() => {
        loadTransactions();
    }, []);

    async function loadTransactions() {
        const response = await fetch("/budget/expenses/currentMonth");
        const data = await response.json();
        // Sort by date (oldest first)
        const sorted = data.sort((a, b) => a.transactionDate.localeCompare(b.transactionDate));
        setTransactions(sorted);
    }

    // Separate expenses and incomes
    const expenses = transactions.filter(t => t.amount < 0);
    const incomes = transactions.filter(t => t.amount >= 0);

    // Calculate daily sums for expenses
    const dailyExpenseSums = expenses.reduce((acc, transaction) => {
        const date = transaction.transactionDate;
        if (!acc[date]) {
            acc[date] = 0;
        }
        acc[date] += transaction.amount;
        return acc;
    }, {});

    // Calculate daily sums for incomes
    const dailyIncomeSums = incomes.reduce((acc, transaction) => {
        const date = transaction.transactionDate;
        if (!acc[date]) {
            acc[date] = 0;
        }
        acc[date] += transaction.amount;
        return acc;
    }, {});

    const sortedExpenseDays = Object.keys(dailyExpenseSums).sort((a, b) => a.localeCompare(b));
    const sortedIncomeDays = Object.keys(dailyIncomeSums).sort((a, b) => a.localeCompare(b));

    let body = <>
        <Col sm={12}>
            <h2>Wydatki i wpływy za obecny miesiąc</h2>
            
            <Row className="mt-3">
                <Col sm={8}>
                    <h4>Wydatki</h4>
                    <TransactionTableReadOnly transactions={expenses} showDate={true} />
                </Col>
                <Col sm={4}>
                    <h4>Podsumowanie wydatków</h4>
                    <Table responsive='sm' striped bordered size="sm">
                        <thead>
                        <tr className='table-info'>
                            <th>Dzień</th>
                            <th style={{textAlign: 'right'}}>Suma</th>
                        </tr>
                        </thead>
                        <tbody>
                        {sortedExpenseDays.map(day => (
                            <tr key={day}>
                                <td>{day}</td>
                                <td style={{textAlign: 'right'}}>{formatNumber(dailyExpenseSums[day])}</td>
                            </tr>
                        ))}
                        </tbody>
                    </Table>
                </Col>
            </Row>

            <Row className="mt-4">
                <Col sm={8}>
                    <h4>Wpływy</h4>
                    <TransactionTableReadOnly transactions={incomes} showDate={true} />
                </Col>
                <Col sm={4}>
                    <h4>Podsumowanie wpływów</h4>
                    <Table responsive='sm' striped bordered size="sm">
                        <thead>
                        <tr className='table-info'>
                            <th>Dzień</th>
                            <th style={{textAlign: 'right'}}>Suma</th>
                        </tr>
                        </thead>
                        <tbody>
                        {sortedIncomeDays.map(day => (
                            <tr key={day}>
                                <td>{day}</td>
                                <td style={{textAlign: 'right'}}>{formatNumber(dailyIncomeSums[day])}</td>
                            </tr>
                        ))}
                        </tbody>
                    </Table>
                </Col>
            </Row>
        </Col>
    </>;
    
    return <Main body={body}/>;
}

export default MonthlyView;
