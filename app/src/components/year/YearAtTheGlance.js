import {Col} from "react-bootstrap";
import React from "react";
import YearlyExpenses from "./YearlyExpenses";
import Main from "../main/Main";

const YearAtTheGlance = () => {
    let body = <>
        <Col sm={1}></Col>
        <Col><YearlyExpenses/></Col>
        <Col sm={1}></Col>
    </>;

    return <Main body={body}/>
}

export default YearAtTheGlance;