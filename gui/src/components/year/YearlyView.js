import React, {useEffect, useState} from "react";
import {Col, Row, Table} from "react-bootstrap";
import {formatNumber, getMonthName, MONTHS_ARRAY, SUM_CATEGORY, SUM_MONTH} from "../../Utils";
import {Cell, Legend, Pie, PieChart, ResponsiveContainer, Tooltip} from "recharts";
import Main from "../main/Main";
import YearFilter from "./YearFilter";
import SankeyComponent from "../monthlyView/SankeyComponent";

const YearlyView = () => {
    const [expenses, setExpenses] = useState([]);
    const [investments, setInvestments] = useState([]);
    const [incomes, setIncomes] = useState([]);
    const [categoryLevels, setCategoryLevels] = useState([]);
    const [year, setYear] = useState(new Date().getFullYear());
    const [topExpenses, setTopExpenses] = useState([]);
    const [expensePieData, setExpensePieData] = useState([]);

    useEffect(() => {
        loadTransactions();
        loadTopExpenses();
        loadExpensePieData();
    }, [year]);

    async function loadTransactions() {
        const response = await fetch(`/budget/expenses/yearGroupedByLevel?year=${year}`);
        const data = await response.json();
        setExpenses(data['expenses']);
        setIncomes(data['incomes']);
        setInvestments(data['investments']);
        setCategoryLevels([...new Set(data['expenses'].map(d => d.category))])
    }

    async function loadTopExpenses() {
        const response = await fetch("/budget/expenses/topExpensesForYear?year=" + year);
        const data = await response.json();
        setTopExpenses(data);
    }

    async function loadExpensePieData() {
        const response = await fetch("/budget/expenses/categoryLevelExpensesForYear?year=" + year);
        const data = await response.json();
        setExpensePieData(data);
    }

    function ExpenseForMonthAndCategory(filteredTransactions, currMonth, currCategory) {

        let expenseAmount = 0;

        if (!(Object.keys(filteredTransactions).length === 0)) {
            let foundExpense = filteredTransactions.find(e => e.month === currMonth && e.category === currCategory);

            if (foundExpense !== undefined) {
                expenseAmount = foundExpense.amount;
            }
        }
        return <td style={{textAlign: 'right'}}>{formatNumber(expenseAmount)}</td>;
    }

    function getTableRows(transactions, currentCategory) {
        return <>
            {MONTHS_ARRAY.map(currentMonth => ExpenseForMonthAndCategory(transactions, currentMonth, currentCategory))}
            {ExpenseForMonthAndCategory(transactions, SUM_MONTH, currentCategory)}
        </>;
    }

    let body = <>
        <Col sm={12}>
            <h2>{`Wydatki i wpływy za ${year}`}</h2>

            <YearFilter
                year={year}
                formHandler={setYear}
            />

            <Row className="mt-3">
                <Col sm={12}>
                    <h4>Sumy miesięczne per poziom kategorii</h4>
                    {(() => {
                        return (<Table id="yearly" responsive='sm' striped bordered size="sm">
                            <thead>
                            <tr className='table-info'>
                                <th></th>
                                {
                                    MONTHS_ARRAY.map(month => {
                                        let monthName = getMonthName(month, 'long');
                                        return <th key={month}
                                                   style={{textAlign: "center"}}>{monthName}</th>
                                    })
                                }
                                <th className="summary">{SUM_CATEGORY}</th>
                            </tr>
                            </thead>
                            <tbody>
                            {categoryLevels.map(currentCategory =>
                                <tr key={currentCategory}>
                                    <td>{currentCategory}</td>
                                    {getTableRows(expenses, currentCategory)}
                                </tr>)
                            }
                            </tbody>
                        </Table>);
                    })()}
                </Col>
            </Row>

            <Row className="mt-4">
                <Col sm={6}>
                    <h4>Wydatki wg poziomów</h4>
                    <ResponsiveContainer width="100%" height={300}>
                        <PieChart>
                            <Pie
                                data={expensePieData}
                                cx="50%"
                                cy="50%"
                                labelLine={false}
                                label={({name, percent}) => `${name}: ${(percent * 100).toFixed(0)}%`}
                                outerRadius={80}
                                fill="#8884d8"
                                dataKey="amount">
                                {expensePieData.map((entry, index) => (
                                    <Cell key={`cell-${index}`} fill={`hsl(${index * 52}, 70%, 50%)`}/>
                                ))}
                            </Pie>
                            <Tooltip formatter={(value) => formatNumber(value)}/>
                            <Legend/>
                        </PieChart>
                    </ResponsiveContainer>
                </Col>
                {topExpenses.length > 0 && (
                    <Col sm={6}>
                        <h4>Największe wydatki</h4>
                        <ResponsiveContainer width="100%" height={300}>
                            <PieChart>
                                <Pie
                                    data={topExpenses}
                                    cx="50%"
                                    cy="50%"
                                    labelLine={false}
                                    label={({name, percent}) => `${name}: ${(percent * 100).toFixed(0)}%`}
                                    outerRadius={80}
                                    fill="#8884d8"
                                    dataKey="amount">
                                    {topExpenses.map((entry, index) => (
                                        <Cell key={`cell-${index}`} fill={`hsl(${index * 36}, 70%, 50%)`}/>
                                    ))}
                                </Pie>
                                <Tooltip
                                    formatter={(value) => formatNumber(value)}
                                    labelFormatter={(label, payload) => payload[0]?.payload?.name || label}
                                />
                            </PieChart>
                        </ResponsiveContainer>
                    </Col>
                )}
            </Row>

            <SankeyComponent
                year={year}
                month={null}
            />

            <Table responsive='sm' striped bordered size="sm">
                <thead>
                <tr className='table-info'>
                    <th></th>
                    {
                        MONTHS_ARRAY.map(month => {
                            let monthName = getMonthName(month, 'long');
                            return <th key={month}
                                       style={{textAlign: "center"}}>{monthName}</th>
                        })
                    }
                    <th className="summary">{SUM_CATEGORY}</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>Wpływy</td>
                    {getTableRows(incomes, "Wpływy")}
                </tr>
                <tr>
                    <td>Inwestycje</td>
                    {getTableRows(investments, "Inwestycje")}
                </tr>
                </tbody>
            </Table>
        </Col>
    </>;

    return <Main body={body}/>;
};

export default YearlyView;
