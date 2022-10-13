import {Col} from "react-bootstrap";
import TransactionTable from "./TransactionTable";
import MoneyAmount from "./MoneyAmount";
import React from "react";
import Main from "../main/Main";
import ExpensesChart from "./ExpensesChart";

const CurrentMonth = () => {
    let body = <>
        <Col sm={8}>
            <TransactionTable mode="currentMonth"/>
        </Col>
        <Col sm={3} className="mt-1">
            <Col sm={6}>
                <MoneyAmount/>
            </Col>
            <ExpensesChart/>
        </Col>
    </>;
    return <Main body={body}/>;
}

export default CurrentMonth;