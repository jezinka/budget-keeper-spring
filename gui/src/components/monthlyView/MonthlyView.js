import {Col} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import TransactionTableReadOnly from "../transactionTable/TransactionTableReadOnly";

const MonthlyView = () => {
    const [transactionsByDay, setTransactionsByDay] = useState({});

    useEffect(() => {
        loadTransactions();
    }, []);

    async function loadTransactions() {
        const response = await fetch("/budget/expenses/currentMonth");
        const data = await response.json();
        
        // Group transactions by day
        const grouped = data.reduce((acc, transaction) => {
            const date = transaction.transactionDate;
            if (!acc[date]) {
                acc[date] = [];
            }
            acc[date].push(transaction);
            return acc;
        }, {});
        
        setTransactionsByDay(grouped);
    }

    // Sort days in ascending order (oldest first)
    const sortedDays = Object.keys(transactionsByDay).sort((a, b) => a.localeCompare(b));

    let body = <>
        <Col sm={12}>
            <h2>Wydatki i wpływy za obecny miesiąc</h2>
            {sortedDays.map(day => (
                <div key={day}>
                    <h5 className="mt-3 mb-2">{day}</h5>
                    <TransactionTableReadOnly transactions={transactionsByDay[day]} />
                </div>
            ))}
        </Col>
    </>;
    
    return <Main body={body}/>;
}

export default MonthlyView;
