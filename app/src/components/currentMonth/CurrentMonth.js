import {Col} from "react-bootstrap";
import TransactionTable from "./TransactionTable";
import MoneyAmount from "./MoneyAmount";
import React from "react";
import Main from "../main/Main";

const CurrentMonth = () => {
    let body = <>
        <Col sm={7}><TransactionTable/></Col>
        <Col sm={2} className="mt-4">
            <MoneyAmount/>
        </Col>
    </>;
    return <Main body={body}/>;
}

export default CurrentMonth;