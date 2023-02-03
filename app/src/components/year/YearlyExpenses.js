import Table from 'react-bootstrap/Table';
import React, {useEffect, useState} from "react";
import Expense from "./Expense";
import {getMonthName, handleError, MONTHS_ARRAY, SUM_CATEGORY, SUM_MONTH} from "../../Utils";
import {Col, Modal} from "react-bootstrap";
import YearFilter from "./YearFilter";
import SpinnerLoadButton from "./SpinnerLoadButton";

export default function YearlyExpenses() {

    const [expenses, setExpenses] = useState([]);
    const [categories, setCategories] = useState([]);
    const [formState, setFormState] = useState({year: new Date().getFullYear()});
    const [transactionsDetails, setTransactionsDetails] = useState([]);
    const [show, setShow] = useState(false);
    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    async function reloadTable() {
        const response = await fetch('/expenses/yearAtTheGlance', {
            method: "POST", body: JSON.stringify(formState), headers: {'Content-Type': 'application/json'}
        });
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
            method: "POST", body: JSON.stringify(formState), headers: {'Content-Type': 'application/json'}
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
        if (currMonth in expenses && currCategory in expenses[currMonth]) {
            const foundExpense = {
                month: currMonth,
                category: currCategory,
                amount: expenses[currMonth][currCategory]
            };

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
        <YearFilter formState={formState} formHandler={setFormState}/>
        <SpinnerLoadButton loadData={reloadPageComponents}/>
        <Col sm={11}>
            <Table id="yearly" responsive='sm' striped bordered size="sm">
                <thead>
                <tr className='table-info'>
                    <th></th>
                    {MONTHS_ARRAY.map(month => <th key={month}
                                                   style={{textAlign: "center"}}>{getMonthName(month, 'long')}</th>)}
                    <th className="summary">{SUM_CATEGORY}</th>
                </tr>
                </thead>
                <tbody>

                {categories.map(currentCategory => <tr key={currentCategory.id}>
                    <td>{currentCategory.name}</td>
                    {MONTHS_ARRAY.map(currentMonth => ExpenseForMonthAndCategory(currentMonth, currentCategory.name))}
                    {ExpenseForMonthAndCategory(SUM_MONTH, currentCategory.name)}
                </tr>)}

                <tr>
                    <td>{SUM_CATEGORY}</td>
                    {MONTHS_ARRAY.map(currentMonth => ExpenseForMonthAndCategory(currentMonth, SUM_CATEGORY))}
                </tr>

                </tbody>
            </Table>
        </Col>
    </>);
}