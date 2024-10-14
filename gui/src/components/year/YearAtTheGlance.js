import React, {useEffect, useState} from "react";
import {getMonthName, handleError, MONTHS_ARRAY, SUM_CATEGORY, SUM_MONTH, UNKNOWN_CATEGORY} from "../../Utils";
import Expense from "./Expense";
import {Col, Modal, Row} from "react-bootstrap";
import YearFilter from "./YearFilter";
import CategoryCheckboxRow from "./CategoryCheckboxRow";
import Table from "react-bootstrap/Table";
import Main from "../main/Main";

export default function YearAtTheGlance() {

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
        let expenseAmount = 0;

        if (expenses.length === 0) {
            expenseAmount = 0;
        } else {
            let foundExpense = expenses.find(e => e.month === currMonth && e.category === currCategory);

            if (foundExpense !== undefined) {
                return <Expense expense={foundExpense} year={year} key={currMonth + currCategory}
                                modalHandler={handleShow} modalContentHandler={setTransactionsDetails}/>;
            }

            if (currMonth === SUM_MONTH && currCategory === SUM_CATEGORY) {
                expenses.forEach(exp => {
                    if (selectedCategories.find(sc => exp.category === sc)) {
                        expenseAmount += exp.amount;
                    }
                });
            } else if (currMonth === SUM_MONTH) {
                expenses.filter(e => e.category === currCategory).forEach(e => {
                    expenseAmount += e.amount;
                });
            } else if (currCategory === SUM_CATEGORY) {
                expenses.filter(e => e.month === currMonth).forEach(e => {
                    if (selectedCategories.find(sc => e.category === sc)) {
                        expenseAmount += e.amount;
                    }
                });
            }
        }

        return <Expense expense={{"amount": expenseAmount, "month": currMonth, "category": currCategory}} year={year}
                        key={currMonth + currCategory}/>;
    }

    const body = <>
        <Modal show={show} onHide={handleClose}>
            <Modal.Header closeButton> <Modal.Title>Transakcje</Modal.Title> </Modal.Header>
            <Modal.Body>{transactionsDetails}</Modal.Body>
        </Modal>
        <Row>
            <YearFilter year={year} formHandler={setYear}/>
        </Row>
        <CategoryCheckboxRow
            categories={categories}
            selectedCategories={selectedCategories}
            setSelectedCategories={setSelectedCategories}
        />
        <Col sm={11}>
            <Table id="yearly" responsive='sm' striped bordered size="sm">
                <thead>
                <tr className='table-info'>
                    <th></th>
                    {
                        MONTHS_ARRAY.map(month => {
                            let monthName = getMonthName(month, 'long');
                            let className = (new Date().getFullYear() == year && new Date().getMonth() + 1) === month ? "current-month current-month-header" : "";
                            return <th key={month} className={className} style={{textAlign: "center"}}>{monthName}</th>
                        })
                    }
                    <th className="summary">{SUM_CATEGORY}</th>
                </tr>
                </thead>
                <tbody>

                {categories.filter(c => c.id === UNKNOWN_CATEGORY)
                    .filter(c => selectedCategories.find(sc => c.name === sc))
                    .map(currentCategory =>
                        <tr className="unknown-category" key={currentCategory.id}>
                            <td>{currentCategory.name}</td>
                            {MONTHS_ARRAY.map(currentMonth => ExpenseForMonthAndCategory(currentMonth, currentCategory.name))}
                            {ExpenseForMonthAndCategory(SUM_MONTH, currentCategory.name)}
                        </tr>
                    )}

                {categories
                    .filter(c => c.id !== UNKNOWN_CATEGORY)
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
    </>;

    return <Main body={body}/>
}