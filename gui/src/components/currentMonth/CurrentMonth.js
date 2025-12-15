import {Badge, Col, Button} from "react-bootstrap";
import TransactionTable from "../transactionTable/TransactionTable";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import ExpensesBarChart from "./ExpensesBarChart";
import {Calendar} from "./Calendar";
import BudgetSummary from "../plan/BudgetSummary";
import EditTransactionModal from "../transactionTable/EditTransactionModal";

const CurrentMonth = () => {
    const [data, setData] = useState([]);
    const [transactions, setTransactions] = useState([]);
    const [withInvestments, setWithInvestments] = useState(false);
    const [showAddModal, setShowAddModal] = useState(false);

    useEffect(() => {
        loadData();
    }, [withInvestments, transactions]);

    useEffect(() => {
        loadData();
        loadTransactions();
    }, []);

    async function loadData() {
        const response = await fetch('/budget/expenses/currentMonthByCategory?withInvestments=' + withInvestments);
        const data = await response.json();
        setData(data);
    }

    async function loadTransactions() {
        const response = await fetch("/budget/expenses/currentMonth");
        const data = await response.json();
        setTransactions(data);
    }

    // modal handles form state

    let body = <>
        <Col sm={8}>
            <TransactionTable transactions={transactions} changeTransactionsHandler={loadTransactions}/>
        </Col>
        <Col sm={4} className="mt-1">
            <BudgetSummary/>
            <Calendar/>
            <h5>
                <Badge bg="light" text="dark" className={"mt-3"}>
                    <input id="withInvestments"
                           type="checkbox"
                           checked={withInvestments}
                           onChange={(e) => {
                               setWithInvestments(e.target.checked);
                           }}
                    /> Inwestycje
                </Badge>
            </h5>

            <h5 className="mt-3">Dodaj wydatek</h5>
            <Button size="sm" variant="primary" onClick={() => setShowAddModal(true)}>Dodaj</Button>
            <EditTransactionModal show={showAddModal} id={null} closeHandler={() => setShowAddModal(false)} changeTransactionsHandler={() => { setShowAddModal(false); loadTransactions(); loadData(); }} />

            <ExpensesBarChart data={data}/>
        </Col>
    </>;
    return <Main body={body}/>;
}

export default CurrentMonth;
