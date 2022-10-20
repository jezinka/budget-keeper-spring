import Table from 'react-bootstrap/Table';
import React, {useEffect, useState} from "react";
import Expense from "./Expense";
import {getMonthName, handleError, MONTHS_ARRAY, SUM_CATEGORY, SUM_MONTH} from "../../Utils";
import {Button, Col, Form, Modal, Spinner} from "react-bootstrap";
import {ArrowClockwise} from "react-bootstrap-icons";

export default function YearlyExpenses() {

    const [expenses, setExpenses] = useState([]);
    const [categories, setCategories] = useState([]);
    const [showSpinner, setShowSpinner] = useState(false);
    const [formState, setFormState] = useState({year: new Date().getFullYear()});
    const [transactionsDetails, setTransactionsDetails] = useState([]);
    const [show, setShow] = useState(false);
    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    const handleChange = (event) => {
        setFormState({...formState, [event.target.name]: event.target.value});
    };

    async function reloadTable() {
        setShowSpinner(true);
        const response = await fetch('/groupedExpenses', {
            method: "POST",
            body: JSON.stringify(formState),
            headers: {'Content-Type': 'application/json'}
        });
        setShowSpinner(false);
        if (response.ok) {
            const data = await response.json();
            if (data) {
                return setExpenses(data)
            }
        }
        return handleError();
    }

    async function fetchCategories() {
        const response = await fetch('/categories/getActiveForSelectYear', {
            method: "POST",
            body: JSON.stringify(formState),
            headers: {'Content-Type': 'application/json'}
        });
        if (response.ok) {
            const data = await response.json();
            if (data) {
                return setCategories(data);
            }
        }
        return handleError();
    }

    function reloadPageComponents() {
        fetchCategories();
        reloadTable();
    }

    useEffect(() => {
        reloadPageComponents();
    }, []);

    function ExpenseForMonthAndCategory(currMonth, currCategory) {
        const foundExpense = expenses.find(({month, category}) => month === currMonth && category === currCategory);
        if (foundExpense !== undefined) {
            return <Expense expense={foundExpense} year={formState.year}
                            modalHandler={handleShow} modalContentHandler={setTransactionsDetails}/>;
        }
        return <td style={{textAlign: 'right'}}>0,00</td>
    }

    return (<>
        <Modal show={show} onHide={handleClose}>
            <Modal.Header closeButton> <Modal.Title>Transakcje</Modal.Title> </Modal.Header>
            <Modal.Body>{transactionsDetails}</Modal.Body>
        </Modal>
        <Col sm={1}>
            <Form>
                <Form.Select className="m-2" size="sm" placeholder="Rok:" onChange={handleChange}
                             name="year" value={formState.year}>
                    {[2022, 2021].map((year) => <option key={year} value={year}>{year}</option>)}
                </Form.Select>
            </Form>
        </Col>
        <Col sm={1}>
            <Button className={"mt-2"} size={"sm"} onClick={reloadPageComponents}>{showSpinner ?
                <Spinner size={"sm"} animation="grow"/> :
                <ArrowClockwise/>}</Button>
        </Col>
        <Col sm={11}>
            <Table id="yearly" responsive='sm' striped bordered size="sm">
                <thead>
                <tr className='table-info'>
                    <th></th>
                    {MONTHS_ARRAY.map(month =>
                        <th key={month} style={{textAlign: "center"}}>{getMonthName(month, 'long')}</th>
                    )}
                    <th className="summary">{SUM_CATEGORY}</th>
                </tr>
                </thead>
                <tbody>

                {categories.map(currentCategory =>
                    <tr key={currentCategory.id}>
                        <td>{currentCategory.name}</td>
                        {MONTHS_ARRAY.map(currentMonth =>
                            ExpenseForMonthAndCategory(currentMonth, currentCategory.name)
                        )}
                        {ExpenseForMonthAndCategory(SUM_MONTH, currentCategory.name)}
                    </tr>
                )}

                <tr>
                    <td>{SUM_CATEGORY}</td>
                    {MONTHS_ARRAY.map(currentMonth =>
                        ExpenseForMonthAndCategory(currentMonth, SUM_CATEGORY)
                    )}
                </tr>

                </tbody>
            </Table>
        </Col>
    </>);
}