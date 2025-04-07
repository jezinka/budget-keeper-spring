import {Button, Col, Form, Pagination, Row, Spinner} from "react-bootstrap";
import Main from "../main/Main";
import React, {useEffect, useState} from "react";
import TransactionCounter from "./TransactionCounter";
import {ArrowClockwise} from "react-bootstrap-icons";
import TransactionTable from "../transactionTable/TransactionTable";

const AllTransactions = () => {
    const [showSpinner, setShowSpinner] = useState(false);
    const [transactions, setTransactions] = useState([]);
    const [transactionCounter, setTransactionCounter] = useState(0);
    const [filterFormState, setFilterFormState] = useState({
        onlyEmptyCategories: false, onlyExpenses: false, title: "", payee: ""
    });

    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 15;

    useEffect(() => {
        loadTransactions();
    }, []);

    const handleFilterChange = (event) => {
        setFilterFormState({...filterFormState, [event.target.name]: event.target.value});
    };

    const handleFilterCheckboxChange = (event) => {
        setFilterFormState({...filterFormState, [event.target.name]: event.target.checked});
    };

    async function loadTransactions() {
        setShowSpinner(true);
        const response = await fetch('/budget/expenses', {
            method: "POST", body: JSON.stringify(filterFormState), headers: {'Content-Type': 'application/json'},
        });
        const data = await response.json();
        setTransactions(data);
        setTransactionCounter(data.length);
        setShowSpinner(false)
    }

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    const paginatedData = () => {
        const startIndex = (currentPage - 1) * itemsPerPage;
        return transactions.slice(startIndex, startIndex + itemsPerPage);
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

                        <Form.Check inline onChange={handleFilterCheckboxChange} name="onlyExpenses" type="switch"
                                    id="onlyExpenses"
                                    label="Only expenses"
                                    value={filterFormState.onlyExpenses}/>
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
        <Col sm={1}>
            <Button onClick={loadTransactions}>{showSpinner ?
                <Spinner size={"sm"} animation="grow"/> : <ArrowClockwise/>}
            </Button>
        </Col>
        <TransactionTable transactions={paginatedData()} changeTransactionsHandler={loadTransactions}/>
        <Row className="justify-content-md-center">
            <Col md={3}>
                <Pagination>
                    {[...Array(Math.ceil(transactionCounter / itemsPerPage)).keys()].map(number => (
                        <Pagination.Item key={number + 1} active={number + 1 === currentPage}
                                         onClick={() => handlePageChange(number + 1)}>
                            {number + 1}
                        </Pagination.Item>
                    ))}
                </Pagination>
            </Col>
        </Row>
    </>;

    return <Main body={body}/>;
}

export default AllTransactions;