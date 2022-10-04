import Table from 'react-bootstrap/Table';
import {useEffect, useState} from "react";

export default function YearlyExpenses() {

    const SUM_MONTH = 99;
    const SUM_CATEGORY = 'SUMA';

    const [expenses, setExpenses] = useState([]);
    const [categories, setCategories] = useState([]);

    function getMonthName(monthNumber) {
        const date = new Date();
        date.setMonth(monthNumber - 1);

        return date.toLocaleString('pl-PL', {month: 'long'});
    }

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

    function ExpenseForMonthAndCategory(currentMonth, currentCategory) {
        const expense = expenses.find(({month, category}) => month === currentMonth && category === currentCategory);
        if (expense !== undefined) {
            return <td>{expense.amount}</td>;
        }
        return <td>0.00</td>;
    }

    return (<Table responsive='sm' striped bordered size="sm">
        <thead>
        <tr className='table-info'>
            <th></th>
            {Array.from({length: 12}, (x, i) => (i + 1)).map(month =>
                <th key={month} style={{textAlign: "center"}}>{getMonthName(month)}</th>
            )}
            <th>Suma</th>
        </tr>
        </thead>
        <tbody>

        {categories.map(currentCategory =>
            <tr key={currentCategory.id}>
                <td>{currentCategory.name}</td>
                {Array.from({length: 12}, (x, i) => (i + 1))
                    .map(currentMonth =>
                        ExpenseForMonthAndCategory(currentMonth, currentCategory.name)
                    )}
                {ExpenseForMonthAndCategory(SUM_MONTH, currentCategory.name)}
            </tr>
        )}

        <tr>
            <td>SUMA</td>
            {Array.from({length: 12}, (x, i) => (i + 1))
                .map(currentMonth =>
                    ExpenseForMonthAndCategory(currentMonth, SUM_CATEGORY)
                )}
        </tr>

        </tbody>
    </Table>);
}