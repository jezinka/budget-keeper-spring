import {Col, Form, Row} from "react-bootstrap";
import TransactionTable from "../currentMonth/TransactionTable";
import Main from "../main/Main";
import React, {useState} from "react";
import TransactionCounter from "./TransactionCounter";

const AllTransactions = () => {
    const [transactionCounter, setTransactionCounter] = useState(0);
    const [filterFormState, setFilterFormState] = useState({
        onlyEmptyCategories: false, onlyEmptyLiabilities: false, onlyOutcome: false, title: "", payee: ""
    });

    const handleFilterChange = (event) => {
        setFilterFormState({...filterFormState, [event.target.name]: event.target.value});
    };

    const handleFilterCheckboxChange = (event) => {
        setFilterFormState({...filterFormState, [event.target.name]: event.target.checked});
    };

    let body = <>
        <Col sm={1}>
            <TransactionCounter transactionCounter={transactionCounter}/>
        </Col>
        <Col sm={8}>
            <Form className="px-4">
                <Row sm={4}>
                    <Col sm={4}>
                        <Form.Check inline onChange={handleFilterCheckboxChange} name="onlyEmptyCategories"
                                    type="switch"
                                    id="onlyEmptyCategories"
                                    label="Only empty categories" value={filterFormState.onlyEmptyCategories}/>

                        <Form.Check inline onChange={handleFilterCheckboxChange} name="onlyEmptyLiabilities"
                                    type="switch"
                                    id="onlyEmptyLiabilities"
                                    label="Only empty liabilities" value={filterFormState.onlyEmptyLiabilities}/>

                        <Form.Check inline onChange={handleFilterCheckboxChange} name="onlyOutcome" type="switch"
                                    id="onlyOutcome"
                                    label="Only outcome"
                                    value={filterFormState.onlyOutcome}/>
                    </Col>
                    <Col sm={3}>
                        <Form.Control size={"sm"} placeholder="Co:" type="text" onChange={handleFilterChange}
                                      name="title" value={filterFormState.title}/>
                    </Col>
                    <Col sm={3}>
                        <Form.Control size={"sm"} placeholder="Kto:" type="text" onChange={handleFilterChange}
                                      name="payee" value={filterFormState.payee}/>
                    </Col>
                </Row>
            </Form>
        </Col>
        <TransactionTable mode="all" counterHandler={setTransactionCounter} filterForm={filterFormState}/>
    </>;

    return <Main body={body}/>;
}

export default AllTransactions;