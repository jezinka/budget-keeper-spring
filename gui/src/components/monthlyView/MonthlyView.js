import {Col, Row} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import {getMonthName} from "../../Utils";
import MonthYearFilter from "./MonthYearFilter";
import SankeyComponent from "./SankeyComponent";
import ExpenseTreeMap from "./ExpenseTreeMap";
import PlanPieChart from "../plan/PlanPieChart";
import ExpensePieChart from "../charts/ExpensePieChart";

const MonthlyView = () => {
    const [year, setYear] = useState(new Date().getFullYear());
    const [month, setMonth] = useState(new Date().getMonth() + 1);
    const [topExpenses, setTopExpenses] = useState([]);
    const [expenses, setExpenses] = useState([]);
    const [expensePieData, setExpensePieData] = useState([]);
    const [planChart, setPlanChart] = useState([]);

    useEffect(() => {
        loadTopExpenses();
        loadExpensePieData();
        loadExpenses();
        loadPlanChart();
    }, [year, month]);

    async function loadTopExpenses() {
        const response = await fetch("/budget/expenses/topExpensesForMonth?year=" + year + "&month=" + month);
        const data = await response.json();
        setTopExpenses(data);
    }

    async function loadExpenses() {
        const response = await fetch("/budget/expenses/getExpensesForMonth?year=" + year + "&month=" + month);
        const data = await response.json();
        setExpenses(data);
    }

    async function loadExpensePieData() {
        const response = await fetch("/budget/expenses/categoryLevelExpensesForMonth?year=" + year + "&month=" + month);
        const data = await response.json();
        setExpensePieData(data);
    }

    async function loadPlanChart() {
        const now = new Date();
        const response = await fetch(`/budget/plans/summary?year=${year}&month=${month}`);
        if (response.ok) setPlanChart((await response.json()).plannedVsUnplanned);
    }

    let body = <>
        <Col sm={11}>
            <h2 className="mt-3">{`Wydatki i wpływy za ${getMonthName(month, 'long')} ${year}`}</h2>

            <MonthYearFilter
                year={year}
                month={month}
                onYearChange={setYear}
                onMonthChange={setMonth}
            />

            <Row className="mt-4">
                {expensePieData.length > 0 && (
                    <Col sm={4}>
                        <h4 className="mt-3">Wydatki wg poziomów</h4>
                        <ExpensePieChart data={expensePieData} hueStep={52} legend/>
                    </Col>
                )}
                {topExpenses.length > 0 && (
                    <Col sm={4}>
                        <h4 className="mt-3">Największe wydatki</h4>
                        <ExpensePieChart data={topExpenses}/>
                    </Col>
                )}
                {planChart.length > 0 && (
                    <Col sm={4}>
                        <h4 className="mt-3">Zaplanowane a poza planem</h4>
                        <PlanPieChart data={planChart}/>
                    </Col>
                )}
            </Row>
            <Row>
                <ExpenseTreeMap expenses={expenses}/>
            </Row>

            <SankeyComponent
                year={year}
                month={month}/>
        </Col>
    </>;

    return <Main body={body}/>;
}

export default MonthlyView;
