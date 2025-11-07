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
        // Sort by date (oldest first) - create copy to avoid mutation
        const sorted = [...data].sort((a, b) => a.transactionDate.localeCompare(b.transactionDate));
        setTransactions(sorted);
    }

    // Separate expenses and incomes
    const expenses = transactions.filter(t => t.amount < 0);
    const incomes = transactions.filter(t => t.amount >= 0);

    // Helper function to calculate daily sums
    const calculateDailySums = (transactions) => transactions.reduce((acc, transaction) => {
        const date = transaction.transactionDate;
        acc[date] = (acc[date] || 0) + transaction.amount;
        return acc;
    }, {});

    const dailyExpenseSums = calculateDailySums(expenses);
    const dailyIncomeSums = calculateDailySums(incomes);

    const sortedExpenseDays = Object.keys(dailyExpenseSums).sort((a, b) => a.localeCompare(b));
    const sortedIncomeDays = Object.keys(dailyIncomeSums).sort((a, b) => a.localeCompare(b));

    // Calculate total sums
    const totalExpenses = expenses.reduce((sum, t) => sum + t.amount, 0);
    const totalIncomes = incomes.reduce((sum, t) => sum + t.amount, 0);

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
                        <tr style={{fontWeight: 'bold', backgroundColor: '#e9ecef'}}>
                            <td>Razem</td>
                            <td style={{textAlign: 'right'}}>{formatNumber(totalExpenses)}</td>
                        </tr>
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
                        <tr style={{fontWeight: 'bold', backgroundColor: '#e9ecef'}}>
                            <td>Razem</td>
                            <td style={{textAlign: 'right'}}>{formatNumber(totalIncomes)}</td>
                        </tr>
                        </tbody>
                    </Table>
                </Col>
            </Row>
        </Col>
    </>;
    
    return <Main body={body}/>;
}

export default MonthlyView;
