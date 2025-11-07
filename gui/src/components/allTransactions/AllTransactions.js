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
        onlyEmptyCategories: false, onlyExpenses: false, description: "", amount: "", dateFrom: "", dateTo: ""
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
        <Col sm={10}>
            <Form className="px-4">
                <Row sm={4}>
                    <Col sm={3}>
                        <Form.Check onChange={handleFilterCheckboxChange} name="onlyEmptyCategories"
                                    type="switch"
                                    id="onlyEmptyCategories"
                                    label="Only empty categories" value={filterFormState.onlyEmptyCategories}/>

                        <Form.Check onChange={handleFilterCheckboxChange} name="onlyExpenses" type="switch"
                                    id="onlyExpenses"
                                    label="Only expenses"
                                    value={filterFormState.onlyExpenses}/>
                    </Col>
                    <Col sm={2}>
                        <Form.Control size={"sm"} placeholder="Opis:" type="text" onChange={handleFilterChange}
                                      name="description" value={filterFormState.description}/>
                    </Col>
                    <Col sm={2}>
                        <Form.Control size={"sm"} placeholder="Kwota:" type="number" step="0.01" onChange={handleFilterChange}
                                      name="amount" value={filterFormState.amount}/>
                    </Col>
                    <Col sm={2}>
                        <Form.Label className="text-muted small mb-0">Data od:</Form.Label>
                        <Form.Control size={"sm"} type="date" onChange={handleFilterChange}
                                      name="dateFrom" value={filterFormState.dateFrom}/>
                    </Col>
                    <Col sm={2}>
                        <Form.Label className="text-muted small mb-0">Data do:</Form.Label>
                        <Form.Control size={"sm"} type="date" onChange={handleFilterChange}
                                      name="dateTo" value={filterFormState.dateTo}/>
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
                    {[...Array(Math.min(10, Math.ceil(transactionCounter / itemsPerPage))).keys()].map(number => (
                        <Pagination.Item key={number + 1} active={number + 1 === currentPage}
                                         onClick={() => handlePageChange(number + 1)}>
                            {number + 1}
                        </Pagination.Item>
                    ))}
                    {Math.ceil(transactionCounter / itemsPerPage) > 10 ? <Pagination.Ellipsis disabled={true}/> : ''}
                </Pagination>
            </Col>
        </Row>
    </>;

    return <Main body={body}/>;
}

export default AllTransactions;