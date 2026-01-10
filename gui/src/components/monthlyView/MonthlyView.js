import {Col, Row} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import {formatNumber, getMonthName} from "../../Utils";
import {Cell, Legend, Pie, PieChart, ResponsiveContainer, Tooltip} from "recharts";
import MonthYearFilter from "./MonthYearFilter";
import SankeyComponent from "./SankeyComponent";

const MonthlyView = () => {
    const [year, setYear] = useState(new Date().getFullYear());
    const [month, setMonth] = useState(new Date().getMonth() + 1);
    const [topExpenses, setTopExpenses] = useState([]);
    const [expensePieData, setExpensePieData] = useState([]);

    useEffect(() => {
        loadTopExpenses();
        loadExpensePieData();
    }, [year, month]);

    async function loadTopExpenses() {
        const response = await fetch("/budget/expenses/topExpensesForMonth?year=" + year + "&month=" + month);
        const data = await response.json();
        setTopExpenses(data);
    }

    async function loadExpensePieData() {
        const response = await fetch("/budget/expenses/categoryLevelExpensesForMonth?year=" + year + "&month=" + month);
        const data = await response.json();
        setExpensePieData(data);
    }

    let body = <>
        <Col sm={12}>
            <h2>{`Wydatki i wpływy za ${getMonthName(month, 'long')} ${year}`}</h2>

            <MonthYearFilter
                year={year}
                month={month}
                onYearChange={setYear}
                onMonthChange={setMonth}
            />

            <Row className="mt-4">
                {expensePieData.length > 0 && (
                    <Col sm={4}>
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
                                <Tooltip formatter={(value) => formatNumber(-value)}/>
                                <Legend/>
                            </PieChart>
                        </ResponsiveContainer>
                    </Col>
                )}
                {topExpenses.length > 0 && (
                    <Col sm={4}>
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
                                    formatter={(value) => formatNumber(-value)}
                                    labelFormatter={(label, payload) => payload[0]?.payload?.fullDescription || label}
                                />
                            </PieChart>
                        </ResponsiveContainer>
                    </Col>
                )}
            </Row>

            <SankeyComponent
                year={year}
                month={month}/>
        </Col>
    </>;

    return <Main body={body}/>;
}

export default MonthlyView;
