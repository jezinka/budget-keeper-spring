import {Col, Row} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import TransactionTableReadOnly from "../transactionTable/TransactionTableReadOnly";
import Table from "react-bootstrap/Table";
import {formatNumber, getMonthName, categoryLevelColors} from "../../Utils";
import {
    Bar,
    BarChart,
    CartesianGrid,
    Cell,
    Legend,
    Pie,
    PieChart,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis
} from "recharts";
import MonthYearFilter from "./MonthYearFilter";
import SankeyComponent from "./SankeyComponent";

const MonthlyView = () => {
    const [transactions, setTransactions] = useState([]);
    const [categoryLevels, setCategoryLevels] = useState([]);
    const [year, setYear] = useState(new Date().getFullYear());
    const [month, setMonth] = useState(new Date().getMonth() + 1);

    useEffect(() => {
        // always load transactions and category levels for the selected year/month
        loadTransactions();
        loadCategoryLevels();
    }, [year, month]);

    async function loadTransactions() {
        const response = await fetch(`/budget/expenses/selectedMonth?year=${year}&month=${month}`);
        const data = await response.json();
        // Sort by date (oldest first) - create copy to avoid mutation
        const sorted = [...data].sort((a, b) => a.transactionDate.localeCompare(b.transactionDate));
        setTransactions(sorted);
    }

    async function loadCategoryLevels() {
        const response = await fetch("/budget/categories/levels");
        const data = await response.json();
        setCategoryLevels(data);
    }

    // Helper function to get category level name
    const getCategoryLevelName = (level) => {
        if (level === null || level === undefined) return "Nieznane";
        const levelInfo = categoryLevels.find(cl => parseInt(cl.level) === level);
        return levelInfo ? levelInfo.name : "Nieznane";
    };

    // Helper function to get color for a category level
    const getColorForLevel = (level, type = 'background') => {
        const colors = categoryLevelColors[level];
        if (colors) {
            return colors[type];
        }

        return type === 'background' ? 'transparent' : '#6c757d'; // default: transparent bg or gray chart
    };

    // Helper function to get background color for transaction rows
    const getRowBackgroundColor = (level, categoryName) => {
        // Special case for "hazard" category
        if (categoryName && categoryName.toLowerCase() === "hazard") {
            return "#ffcccc"; // light red
        }

        return getColorForLevel(level, 'background');
    };

    // Helper function to get chart color based on category level
    const getChartColor = (level) => {
        return getColorForLevel(level, 'chart');
    };

    // Separate expenses and incomes
    const expenses = transactions.filter(t => Number(t.categoryLevel) !== 4);
    const incomes = transactions.filter(t => Number(t.categoryLevel) === 4);

    // Helper function to calculate daily sums (for summary table)
    const calculateDailySums = (transactions) => transactions
        .filter(t => t.categoryLevel !== 2)
        .reduce((acc, transaction) => {
            const date = transaction.transactionDate;
            acc[date] = (acc[date] || 0) + transaction.amount;
            return acc;
        }, {});

    const dailyExpenseSums = calculateDailySums(expenses);
    const dailyIncomeSums = calculateDailySums(incomes);

    const sortedExpenseDays = Object.keys(dailyExpenseSums).sort((a, b) => a.localeCompare(b));
    const sortedIncomeDays = Object.keys(dailyIncomeSums).sort((a, b) => a.localeCompare(b));

    // Helper function to calculate sums by category level (for pie chart)
    const calculateCategoryLevelSums = (transactions) => {
        return transactions
            .reduce((acc, transaction) => {
                const level = transaction.categoryLevel !== null && transaction.categoryLevel !== undefined
                    ? transaction.categoryLevel
                    : -1;
                const levelName = getCategoryLevelName(level);

                if (!acc[level]) {
                    acc[level] = {
                        sum: 0,
                        levelName: levelName
                    };
                }
                acc[level].sum += transaction.amount;
                return acc;
            }, {});
    };

    const categoryLevelExpenseSums = calculateCategoryLevelSums(expenses);

    // Sort by level
    const sortedExpenseLevels = Object.keys(categoryLevelExpenseSums)
        .map(k => parseInt(k))
        .sort((a, b) => a - b);

    // Calculate total sums
    const totalExpenses = expenses.reduce((sum, t) => sum + t.amount, 0);
    const totalIncomes = incomes.reduce((sum, t) => sum + t.amount, 0);

    // Prepare data for pie charts
    const expensePieData = sortedExpenseLevels
        .filter(t => t !== 4)
        .map(level => ({
            name: categoryLevelExpenseSums[level].levelName,
            value: Math.abs(categoryLevelExpenseSums[level].sum),
            level: level
        }));

    // Prepare data for top expenses pie chart (per transaction)
    const topExpenses = [...expenses]
        .filter(t => t.categoryLevel !== 2 && t.categoryLevel !== 4)
        .sort((a, b) => a.amount - b.amount) // Most negative first
        .slice(0, 10) // Top 10 expenses
        .map(t => ({
            name: t.description.substring(0, 30) + (t.description.length > 30 ? '...' : ''),
            value: Math.abs(t.amount),
            fullDescription: t.description
        }));

    // Prepare data for daily expenses bar chart
    const getDayOfWeek = (dateString) => {
        const days = ['Nd', 'Pn', 'Wt', 'Śr', 'Cz', 'Pt', 'So'];
        const date = new Date(dateString);
        return days[date.getDay()];
    };

    const dailyExpensesChartData = sortedExpenseDays.map(day => ({
        date: day,
        dayOfWeek: getDayOfWeek(day),
        amount: Math.abs(dailyExpenseSums[day]),
        label: `${day.substring(5)} (${getDayOfWeek(day)})`
    }));

    const maxDailyExpense = dailyExpensesChartData.length > 0 ? Math.max(...dailyExpensesChartData.map(d => d.amount)) : 0;

    let body = <>
        <Col sm={12}>
            <h2>{`Wydatki i wpływy za ${getMonthName(month, 'long')} ${year}`}</h2>

            <MonthYearFilter
                year={year}
                month={month}
                onYearChange={setYear}
                onMonthChange={setMonth}
            />

            <Row className="mt-3">
                <Col sm={8}>
                    <h4>Wpływy</h4>
                    <TransactionTableReadOnly transactions={incomes} showDate={true}
                                              getRowColor={getRowBackgroundColor}/>
                </Col>
                <Col sm={4}>
                    <h4>Podsumowanie</h4>
                    <Table responsive='sm' striped bordered size="sm">
                        <thead>
                        <tr className='table-info'>
                            <th>Dzień</th>
                            <th style={{textAlign: 'right'}}>Suma</th>
                        </tr>
                        </thead>
                        <tbody>
                        {sortedIncomeDays.map(day => (
                            <tr key={day}>
                                <td>{day}</td>
                                <td style={{textAlign: 'right'}}>{formatNumber(dailyIncomeSums[day])}</td>
                            </tr>
                        ))}
                        <tr style={{fontWeight: 'bold', backgroundColor: '#e9ecef'}}>
                            <td>Razem</td>
                            <td style={{textAlign: 'right'}}>{formatNumber(totalIncomes)}</td>
                        </tr>
                        </tbody>
                    </Table>
                </Col>
            </Row>

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
                                    dataKey="value"
                                >
                                    {expensePieData.map((entry, index) => (
                                        <Cell key={`cell-${index}`} fill={getChartColor(entry.level)}/>
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
                                    dataKey="value"
                                >
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
                {dailyExpensesChartData.length > 0 && (
                    <Col sm={4}>
                        <h4>Wydatki dzienne</h4>
                        <ResponsiveContainer width="100%" height={300}>
                            <BarChart data={dailyExpensesChartData}>
                                <CartesianGrid strokeDasharray="3 3"/>
                                <XAxis dataKey="dayOfWeek" tick={{fontSize: 12}}/>
                                <YAxis tick={{fontSize: 10}} tickFormatter={(value) => formatNumber(-value)}/>
                                <Tooltip formatter={(value) => formatNumber(-value)}
                                         labelFormatter={(label, payload) => payload[0]?.payload?.label || label}/>
                                <Bar dataKey="amount">
                                    {dailyExpensesChartData.map((entry, index) => (
                                        <Cell key={`cell-${index}`}
                                              fill={entry.amount === maxDailyExpense ? '#dc3545' : (entry.dayOfWeek === 'So' || entry.dayOfWeek === 'Nd' ? '#ffc107' : '#6c757d')}/>
                                    ))}
                                </Bar>
                            </BarChart>
                        </ResponsiveContainer>
                    </Col>
                )}
            </Row>

            <SankeyComponent
                year={year}
                month={month}/>

            <Row className="mt-4">
                <h4>Wydatki</h4>
                <Col sm={8}>
                    <TransactionTableReadOnly transactions={expenses} showDate={true}
                                              getRowColor={getRowBackgroundColor}/>
                </Col>
                <Col sm={4}>
                    <Table responsive='sm' striped bordered size="sm">
                        <thead>
                        <tr className='table-info'>
                            <th>Dzień</th>
                            <th style={{textAlign: 'right'}}>Suma</th>
                        </tr>
                        </thead>
                        <tbody>
                        {sortedExpenseDays.map(day => (
                            <tr key={day}>
                                <td>{day}</td>
                                <td style={{textAlign: 'right'}}>{formatNumber(dailyExpenseSums[day])}</td>
                            </tr>
                        ))}
                        <tr style={{fontWeight: 'bold', backgroundColor: '#e9ecef'}}>
                            <td>Razem</td>
                            <td style={{textAlign: 'right'}}>{formatNumber(totalExpenses)}</td>
                        </tr>
                        </tbody>
                    </Table>
                </Col>
            </Row>
        </Col>
    </>;

    return <Main body={body}/>;
}

export default MonthlyView;
