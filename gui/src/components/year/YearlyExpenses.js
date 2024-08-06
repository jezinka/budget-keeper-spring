import Table from 'react-bootstrap/Table';
import React, {useEffect, useState} from "react";
import Expense from "./Expense";
import {getMonthName, handleError, MONTHS_ARRAY, SUM_CATEGORY, SUM_MONTH} from "../../Utils";
import {Col, Modal, Row} from "react-bootstrap";
import YearFilter from "./YearFilter";
import CategoryCheckboxRow from "./CategoryCheckboxRow";

export default function YearlyExpenses() {

    const [expenses, setExpenses] = useState([]);
    const [categories, setCategories] = useState([]);
    const [selectedCategories, setSelectedCategories] = useState([]);
    const [year, setYear] = useState(new Date().getFullYear());
    const [transactionsDetails, setTransactionsDetails] = useState([]);
    const [show, setShow] = useState(false);
    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    async function reloadTable() {
        const response = await fetch('/budget/expenses/yearAtTheGlance/' + year);
        if (response.ok) {
            const data = await response.json();
            if (data) {
                return setExpenses(data)
            }
        }
        return handleError();
    }

    async function fetchCategories() {
        const response = await fetch('/budget/categories/getActiveForSelectedYear/' + year);

        if (response.ok) {
            const data = await response.json();
            if (data) {
                setSelectedCategories(data.map(c => c.name));
                return setCategories(data);
            }
        }
        return handleError();
    }

    useEffect(() => {
        fetchCategories();
        reloadTable();
    }, [year])

    function ExpenseForMonthAndCategory(currMonth, currCategory) {
        if (expenses.length === 0) {
            return <td key={currMonth + currCategory} style={{textAlign: 'right'}}>0,00</td>;
        }
        let foundExpense = expenses.find(e => e.month === currMonth && e.category === currCategory);
        if (foundExpense !== undefined) {
            return <Expense expense={foundExpense} year={year} key={currMonth + currCategory}
                            modalHandler={handleShow} modalContentHandler={setTransactionsDetails}/>;
        } else if (currMonth === SUM_MONTH && currCategory === SUM_CATEGORY) {
            let expenseAmount = 0;
            expenses.forEach(exp => {
                if (selectedCategories.find(sc => exp.category === sc)) {
                    expenseAmount += exp.amount;
                }
            });
            return <Expense expense={{"amount": expenseAmount}} year={year} key={currMonth + currCategory}/>;
        } else if (currMonth === SUM_MONTH) {
            let expenseAmount = 0;

            expenses.filter(e => e.category === currCategory).forEach(e => {
                expenseAmount += e.amount;
            })

            return <Expense expense={{"amount": expenseAmount}} year={year} key={currMonth + currCategory}/>;
        } else if (currCategory === SUM_CATEGORY) {
            let expenseAmount = 0;
            expenses.filter(e => e.month === currMonth).forEach(e => {
                if (selectedCategories.find(sc => e.category === sc)) {
                    expenseAmount += e.amount;
                }
            });
            return <Expense expense={{"amount": expenseAmount}} year={year} key={currMonth + currCategory}/>;
        } else {
            return <td key={currMonth + currCategory} style={{textAlign: 'right'}}>0,00</td>
        }
    }

    return (<>
        <Modal show={show} onHide={handleClose}>
            <Modal.Header closeButton> <Modal.Title>Transakcje</Modal.Title> </Modal.Header>
            <Modal.Body>{transactionsDetails}</Modal.Body>
        </Modal>
        <Row>
            <YearFilter year={year} formHandler={setYear}/>
        </Row>
        <CategoryCheckboxRow
            categories={categories.map(c => c.name)}
            selectedCategories={selectedCategories}
            setSelectedCategories={setSelectedCategories}
        />
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

                {categories
                    .filter(c => selectedCategories.find(sc => c.name === sc))
                    .map(currentCategory =>
                        <tr key={currentCategory.id}>
                            <td>{currentCategory.name}</td>
                            {MONTHS_ARRAY.map(currentMonth => ExpenseForMonthAndCategory(currentMonth, currentCategory.name))}
                            {ExpenseForMonthAndCategory(SUM_MONTH, currentCategory.name)}
                        </tr>)}
                <tr>
                    <td>{SUM_CATEGORY}</td>
                    {MONTHS_ARRAY.map(currentMonth => ExpenseForMonthAndCategory(currentMonth, SUM_CATEGORY))}
                    {ExpenseForMonthAndCategory(SUM_MONTH, SUM_CATEGORY)}
                </tr>
                </tbody>
            </Table>
        </Col>
    </>);
}