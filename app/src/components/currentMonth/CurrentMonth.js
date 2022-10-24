import {Col} from "react-bootstrap";
import TransactionTable from "./TransactionTable";
import MoneyAmount from "./MoneyAmount";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import ExpensesBarChart from "./ExpensesBarChart";
import ExpensesPieChart from "./ExpensesPieChart";
import DailyBarChart from "./DailyBarChart";

const CurrentMonth = () => {
    const [data, setData] = useState([])

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        const response = await fetch('/groupedExpenses/currentMonthByCategory')
        const data = await response.json();
        setData(data);
    }

    let body = <>
        <Col sm={8}>
            <TransactionTable mode="currentMonth" reloadCharts={loadData}/>
        </Col>
        <Col sm={3} className="mt-1">
            <Col sm={6}>
                <MoneyAmount/>
            </Col>
            <DailyBarChart/>
            <ExpensesBarChart data={data}/>
            <ExpensesPieChart data={data}/>
        </Col>
    </>;
    return <Main body={body}/>;
}

export default CurrentMonth;