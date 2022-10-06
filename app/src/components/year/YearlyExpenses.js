import Table from 'react-bootstrap/Table';
import {useEffect, useState} from "react";
import Expense from "./Expense";
import {getMonthName, MONTHS_ARRAY, SUM_CATEGORY, SUM_MONTH, SUMMARY_STYLE} from "../../Utils";

export default function YearlyExpenses() {

    const [expenses, setExpenses] = useState([]);
    const [categories, setCategories] = useState([]);

    async function reloadTable() {
        const response = await fetch('/groupedExpenses');
        const data = await response.json();
        setExpenses(data);
    }

    async function fetchCategories() {
        const response = await fetch('/categories/getActiveForCurrentYear');
        const data = await response.json();
        setCategories(data);
    }

    useEffect(() => {
        fetchCategories();
        reloadTable();
    }, []);

    function ExpenseForMonthAndCategory(currMonth, currCategory) {
        const foundExpense = expenses.find(({month, category}) => month === currMonth && category === currCategory);
        return <Expense expense={foundExpense}/>;
    }

    return (<Table responsive='sm' striped bordered size="sm">
        <thead>
        <tr className='table-info'>
            <th></th>
            {MONTHS_ARRAY.map(month =>
                <th key={month} style={{textAlign: "center"}}>{getMonthName(month)}</th>
            )}
            <th style={SUMMARY_STYLE}>{SUM_CATEGORY}</th>
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

        <tr style={SUMMARY_STYLE}>
            <td>{SUM_CATEGORY}</td>
            {MONTHS_ARRAY.map(currentMonth =>
                ExpenseForMonthAndCategory(currentMonth, SUM_CATEGORY)
            )}
        </tr>

        </tbody>
    </Table>);
}