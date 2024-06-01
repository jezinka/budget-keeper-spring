import {Col, Row} from "react-bootstrap";
import TransactionTable from "./TransactionTable";
import MoneyAmount from "./MoneyAmount";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import ExpensesBarChart from "./ExpensesBarChart";
import ExpensesPieChart from "./ExpensesPieChart";
import {Calendar} from "./Calendar";

const CurrentMonth = () => {
    const [data, setData] = useState([])

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        const response = await fetch('/budget/expenses/currentMonthByCategory')
        const data = await response.json();
        setData(data);
    }

    let body = <>
        <Col sm={8}>
            <TransactionTable mode="currentMonth" reloadCharts={loadData}/>
        </Col>
        <Col sm={4} className="mt-1">
            <Row>
                <MoneyAmount/>
            </Row>
            <ExpensesBarChart data={data}/>
            <Calendar/>
            <ExpensesPieChart data={data}/>
        </Col>
    </>;
    return <Main body={body}/>;
}

export default CurrentMonth;