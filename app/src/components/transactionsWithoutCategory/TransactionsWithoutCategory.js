import {Col} from "react-bootstrap";
import TransactionTable from "../currentMonth/TransactionTable";
import Main from "../main/Main";
import React from "react";

const TransactionsWithoutCategory = () => {
    let body = <>
        <Col><TransactionTable mode="withoutCategory"/></Col>
        <Col sm={2} className="mt-1"/>
    </>;
    return <Main body={body}/>;
}

export default TransactionsWithoutCategory;