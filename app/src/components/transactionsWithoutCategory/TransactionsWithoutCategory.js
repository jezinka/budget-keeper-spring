import {Col} from "react-bootstrap";
import TransactionTable from "../currentMonth/TransactionTable";
import Main from "../main/Main";
import React, {useState} from "react";
import TransactionCounter from "./TransactionCounter";

const TransactionsWithoutCategory = () => {
    const [transactionCounter, setTransactionCounter] = useState(0);

    let body = <>
        <Col><TransactionTable mode="withoutCategory" counterHandler={setTransactionCounter}/></Col>
        <Col sm={2} className="mt-1">
            <TransactionCounter transactionCounter={transactionCounter}/>
        </Col>
    </>;
    return <Main body={body}/>;
}

export default TransactionsWithoutCategory;