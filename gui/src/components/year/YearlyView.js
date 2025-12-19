import React, {useEffect, useState} from "react";
import {Col, Modal, Row, Table} from "react-bootstrap";
import {categoryLevelColors, formatNumber, getMonthName, SUM_MONTH} from "../../Utils";
import {Cell, Legend, Pie, PieChart, ResponsiveContainer, Tooltip} from "recharts";
import Main from "../main/Main";
import YearFilter from "./YearFilter";
import Expense from "./Expense";

const YearlyView = () => {
    const [yearlyData, setYearlyData] = useState(null);
    const [year, setYear] = useState(new Date().getFullYear());
    const [transactionsDetails, setTransactionsDetails] = useState([]);
    const [show, setShow] = useState(false);
    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    useEffect(() => {
        loadYearlyData();
    }, [year]);

    async function loadYearlyData() {
        const response = await fetch(`/budget/expenses/yearlyView/${year}`);
        const data = await response.json();
        setYearlyData(data);
    }

    // Helper function to get color for a category level
    const getColorForLevel = (level, type = 'background') => {
        const colors = categoryLevelColors[level];
        if (colors) {
            return colors[type];
        }
        return type === 'background' ? 'transparent' : '#6c757d';
    };

    // Helper function to get chart color based on category level
    const getChartColor = (level) => getColorForLevel(level, 'chart');

    if (!yearlyData) {
        return <Main body={<Col sm={12}><h2>Ładowanie...</h2></Col>} />;
    }

    const months = Array.from({length: 12}, (_, i) => i + 1);

    // Calculate monthly totals for footer
    const monthTotals = months.map(m => 
        yearlyData.categoryLevels.reduce((acc, level) => 
            acc + (level.monthlySums[m] || 0), 0
        )
    );
    const grandTotal = monthTotals.reduce((a, b) => a + b, 0);

    let body = <>
        <Modal show={show} onHide={handleClose}>
            <Modal.Header closeButton>
                <Modal.Title>Transakcje</Modal.Title>
            </Modal.Header>
            <Modal.Body>{transactionsDetails}</Modal.Body>
        </Modal>
        <Col sm={12}>
            <h2>{`Wydatki i wpływy za ${year}`}</h2>

            <YearFilter
                year={year}
                formHandler={setYear}
            />

            <Row className="mt-3">
                <Col sm={12}>
                    <h4>Sumy miesięczne per poziom kategorii</h4>
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
                        {yearlyData.categoryLevels.map(levelData => (
                            <tr key={levelData.level}>
                                <td>{levelData.name}</td>
                                {months.map(m => (
                                    <Expense
                                        key={m}
                                        expense={{
                                            amount: levelData.monthlySums[m] || 0,
                                            month: m,
                                            category: levelData.name,
                                            categoryLevel: levelData.level,
                                            transactionCount: 1
                                        }}
                                        year={year}
                                        modalHandler={handleShow}
                                        modalContentHandler={setTransactionsDetails}
                                    />
                                ))}
                                <Expense
                                    key="total"
                                    expense={{
                                        amount: levelData.totalSum,
                                        month: SUM_MONTH,
                                        category: levelData.name,
                                        categoryLevel: levelData.level,
                                        transactionCount: 0
                                    }}
                                    year={year}
                                />
                            </tr>
                        ))}
                        </tbody>
                        <tfoot>
                        <tr style={{fontWeight: 'bold', backgroundColor: '#e9ecef'}}>
                            <td>Razem</td>
                            {monthTotals.map((t, idx) => (
                                <Expense
                                    key={idx}
                                    expense={{
                                        amount: t,
                                        month: idx + 1,
                                        category: 'SUMA',
                                        transactionCount: 0
                                    }}
                                    year={year}
                                />
                            ))}
                            <Expense
                                key="grand-total"
                                expense={{
                                    amount: grandTotal,
                                    month: SUM_MONTH,
                                    category: 'SUMA',
                                    transactionCount: 0
                                }}
                                year={year}
                            />
                        </tr>
                        </tfoot>
                    </Table>
                </Col>
            </Row>

            <Row className="mt-4">
                <Col sm={6}>
                    <h4>Wydatki wg poziomów</h4>
                    <ResponsiveContainer width="100%" height={300}>
                        <PieChart>
                            <Pie
                                data={yearlyData.expensePieData}
                                cx="50%"
                                cy="50%"
                                labelLine={false}
                                label={({name, percent}) => `${name}: ${(percent * 100).toFixed(0)}%`}
                                outerRadius={80}
                                fill="#8884d8"
                                dataKey="value">
                                {yearlyData.expensePieData.map((entry, index) => (
                                    <Cell key={`cell-${index}`} fill={getChartColor(entry.level)}/>
                                ))}
                            </Pie>
                            <Tooltip formatter={(value) => formatNumber(-value)}/>
                            <Legend/>
                        </PieChart>
                    </ResponsiveContainer>
                </Col>
                {yearlyData.topExpenses.length > 0 && (
                    <Col sm={6}>
                        <h4>Największe wydatki</h4>
                        <ResponsiveContainer width="100%" height={300}>
                            <PieChart>
                                <Pie
                                    data={yearlyData.topExpenses}
                                    cx="50%"
                                    cy="50%"
                                    labelLine={false}
                                    label={({name, percent}) => `${name}: ${(percent * 100).toFixed(0)}%`}
                                    outerRadius={80}
                                    fill="#8884d8"
                                    dataKey="value"
                                >
                                    {yearlyData.topExpenses.map((entry, index) => (
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
            {yearlyData.totalIncomeYear > 0 && (
                <Row className="mt-3">
                    <Col sm={12}>
                        <h4>Wpływy</h4>
                        <Table responsive='sm' striped bordered size="sm">
                            <thead>
                            <tr className='table-info'>
                                <th>Pozycja</th>
                                {months.map(m => <th key={m}
                                                     style={{textAlign: 'right'}}>{getMonthName(m, 'short')}</th>)}
                                <th style={{textAlign: 'right'}}>Razem</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>Wpływy</td>
                                {yearlyData.monthlyIncomeSums.map((val, idx) => (
                                    <Expense
                                        key={idx}
                                        expense={{
                                            amount: val,
                                            month: idx + 1,
                                            category: 'Wpływy',
                                            categoryLevel: 4,
                                            transactionCount: 1
                                        }}
                                        year={year}
                                        modalHandler={handleShow}
                                        modalContentHandler={setTransactionsDetails}
                                    />
                                ))}
                                <Expense
                                    key="total"
                                    expense={{
                                        amount: yearlyData.totalIncomeYear,
                                        month: SUM_MONTH,
                                        category: 'Wpływy',
                                        categoryLevel: 4,
                                        transactionCount: 0
                                    }}
                                    year={year}
                                />
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
