import {Col} from "react-bootstrap";
import TransactionTable from "./TransactionTable";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import ExpensesBarChart from "./ExpensesBarChart";
import ExpensesPieChart from "./ExpensesPieChart";
import {Calendar} from "./Calendar";
import BudgetSummary from "../plan/BudgetSummary";

const CurrentMonth = () => {
    const [data, setData] = useState([]);
    const [withInvestments, setWithInvestments] = useState(true);

    useEffect(() => {
        loadData();
    }, [withInvestments]);

    async function loadData() {
        const response = await fetch('/budget/expenses/currentMonthByCategory?withInvestments=' + withInvestments);
        const data = await response.json();
        setData(data);
    }

    let body = <>
        <Col sm={8}>
            <TransactionTable mode="currentMonth" reloadCharts={loadData}/>
        </Col>
        <Col sm={4} className="mt-1">
            <BudgetSummary/>
            <input id="withInvestments"
                   type="checkbox"
                   checked={withInvestments}
                   onChange={(e) => {setWithInvestments(e.target.checked);}}
            />
            <span className="fw-bold">Inwestycje</span>
            <ExpensesBarChart data={data}/>
            <Calendar/>
            <ExpensesPieChart data={data}/>
        </Col>
    </>;
    return <Main body={body}/>;
}

export default CurrentMonth;