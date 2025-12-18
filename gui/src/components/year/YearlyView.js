import React, {useEffect, useState} from "react";
import {Col, Row, Table} from "react-bootstrap";
import {categoryLevelColors, formatNumber, getMonthName} from "../../Utils";
import {Cell, Legend, Pie, PieChart, ResponsiveContainer, Tooltip} from "recharts";
import Main from "../main/Main";
import YearFilter from "./YearFilter";

const YearlyView = () => {
    const [transactions, setTransactions] = useState([]);
    const [categoryLevels, setCategoryLevels] = useState([]);
    const [year, setYear] = useState(new Date().getFullYear());

    useEffect(() => {
        // always load transactions and category levels for the selected year/month
        loadTransactions();
        loadCategoryLevels();
    }, [year]);

    async function loadTransactions() {
        const response = await fetch(`/budget/expenses/selectedYear?year=${year}`);
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

    // Helper function to get chart color based on category level
    const getChartColor = (level) => getColorForLevel(level, 'chart');

    // Separate expenses and incomes
    const expenses = transactions.filter(t => t.categoryLevel != 4);
    const incomes = transactions.filter(t => t.categoryLevel == 4);

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
                acc[level].sum += Math.abs(transaction.amount);
                return acc;
            }, {});
    };

    // totals per level computed from the raw expenses (all negative transactions)
    const levelTotalsAll = calculateCategoryLevelSums(expenses);
    const categoryLevelExpenseSums = levelTotalsAll;

    // Sort by level
    const sortedExpenseLevels = Object.keys(categoryLevelExpenseSums)
        .map(k => parseInt(k))
        .sort((a, b) => a - b);

    // Prepare data for pie charts
    // Pie uses a filtered view (exclude level 4) but table below must use `levelTotalsAll` (unfiltered)
    const expensePieData = sortedExpenseLevels
        .filter(t => t !== 4)
        .map(level => ({
            name: categoryLevelExpenseSums[level].levelName,
            value: categoryLevelExpenseSums[level].sum,
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

    // --- new: monthly sums per category level (expenses only) ---
    // build list of levels to display (use categoryLevels if available, otherwise derive from data)
    const levelsListRaw = (categoryLevels && categoryLevels.length > 0)
        ? categoryLevels.map(cl => ({level: parseInt(cl.level), name: cl.name})).sort((a, b) => a.level - b.level)
        : Object.keys(levelTotalsAll).map(k => ({
            level: parseInt(k),
            name: levelTotalsAll[k].levelName
        })).sort((a, b) => a.level - b.level);

    // main table should not include category level 4 (wpływy) — extract levels for main table
    const levelsForMain = levelsListRaw.filter(l => l.level !== 4);

    // initialize monthly-level sums 1..12 for all levels (we'll use levelsForMain when rendering main table)
    const monthlyLevelSums = {};
    for (let m = 1; m <= 12; m++) {
        monthlyLevelSums[m] = {};
        levelsListRaw.forEach(l => monthlyLevelSums[m][l.level] = 0);
    }

    // aggregate expenses per month and per level
    expenses.forEach(e => {
        const d = new Date(e.transactionDate);
        const m = d.getMonth() + 1;
        const lvl = e.categoryLevel !== null && e.categoryLevel !== undefined ? e.categoryLevel : -1;
        if (!monthlyLevelSums[m]) monthlyLevelSums[m] = {};
        monthlyLevelSums[m][lvl] = (monthlyLevelSums[m][lvl] || 0) + e.amount;
    });

    // --- new: monthly sums for incomes (category level 4) computed from positive transactions ---
    const monthlyIncomeSums = Array.from({length: 12}, () => 0);
    incomes.forEach(t => {
        const m = new Date(t.transactionDate).getMonth(); // 0..11
        monthlyIncomeSums[m] = (monthlyIncomeSums[m] || 0) + t.amount;
    });
    const totalIncomeYear = monthlyIncomeSums.reduce((a, b) => a + b, 0);

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
                    {/* Precompute column totals */}
                    {(() => {
                        const months = Array.from({length: 12}, (_, i) => i + 1);
                        // monthTotals for main table (exclude incomes level 4)
                        const monthTotals = months.map(m => levelsForMain.reduce((acc, l) => acc + (monthlyLevelSums[m][l.level] || 0), 0));
                        const grandTotal = monthTotals.reduce((a, b) => a + b, 0);

                        return (
                            <Table responsive='sm' striped bordered size="sm">
                                <thead>
                                <tr className='table-info'>
                                    <th>Kategoria</th>
                                    {months.map(m => <th key={m}
                                                         style={{textAlign: 'right'}}>{getMonthName(m, 'long')}</th>)}
                                    <th style={{textAlign: 'right'}}>Razem</th>
                                </tr>
                                </thead>
                                <tbody>
                                {levelsForMain.map(l => {
                                    const rowTotal = months.reduce((acc, m) => acc + (monthlyLevelSums[m][l.level] || 0), 0);
                                    if (rowTotal === 0) return null; // skip levels with zero total
                                    return (
                                        <tr key={l.level}>
                                            <td>{l.name}</td>
                                            {months.map(m => (
                                                <td key={m}
                                                    style={{textAlign: 'right'}}>{formatNumber(monthlyLevelSums[m][l.level] || 0)}</td>
                                            ))}
                                            <td style={{textAlign: 'right'}}>{formatNumber(rowTotal)}</td>
                                        </tr>
                                    );
                                })}
                                </tbody>
                                <tfoot>
                                <tr style={{fontWeight: 'bold', backgroundColor: '#e9ecef'}}>
                                    <td>Razem</td>
                                    {monthTotals.map((t, idx) => <td key={idx}
                                                                     style={{textAlign: 'right'}}>{formatNumber(t)}</td>)}
                                    <td style={{textAlign: 'right'}}>{formatNumber(grandTotal)}</td>
                                </tr>
                                </tfoot>
                            </Table>
                        );
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
                                dataKey="value">
                                {expensePieData.map((entry, index) => (
                                    <Cell key={`cell-${index}`} fill={getChartColor(entry.level)}/>
                                ))}
                            </Pie>
                            <Tooltip formatter={(value) => formatNumber(-value)}/>
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
            </Row>

            {/* incomes (level 4) in separate table */}
            {totalIncomeYear > 0 && (
                <Row className="mt-3">
                    <Col sm={12}>
                        <h4>Wpływy</h4>
                        <Table responsive='sm' striped bordered size="sm">
                            <thead>
                            <tr className='table-info'>
                                <th>Pozycja</th>
                                {Array.from({length: 12}, (_, i) => i + 1).map(m => <th key={m}
                                                                                        style={{textAlign: 'right'}}>{getMonthName(m, 'short')}</th>)}
                                <th style={{textAlign: 'right'}}>Razem</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>Wpływy</td>
                                {monthlyIncomeSums.map((val, idx) => <td key={idx}
                                                                         style={{textAlign: 'right'}}>{formatNumber(val)}</td>)}
                                <td style={{textAlign: 'right'}}>{formatNumber(totalIncomeYear)}</td>
                            </tr>
                            </tbody>
                        </Table>
                    </Col>
                </Row>
            )}
        </Col>
    </>;

    return <Main body={body}/>;
};

export default YearlyView;
