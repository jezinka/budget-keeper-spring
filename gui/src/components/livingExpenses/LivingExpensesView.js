import React, {useEffect, useState} from "react";
import {Col, Row, Table} from "react-bootstrap";
import {formatNumber, getMonthName, monthColors, MONTHS_ARRAY} from "../../Utils";
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
import Main from "../main/Main";
import CategoryCheckboxRow from "../year/CategoryCheckboxRow";
import MonthYearFilter from "../monthlyView/MonthYearFilter";

const LivingExpensesView = () => {
    const [categories, setCategories] = useState([]);
    const [selectedCategories, setSelectedCategories] = useState([]);
    const [topExpenses, setTopExpenses] = useState([]);
    const [data, setData] = useState({});
    const [year, setYear] = useState(new Date().getFullYear());
    const [month, setMonth] = useState(new Date().getMonth() + 1);

    useEffect(() => {
        const currentYear = new Date().getFullYear();
        fetch("/budget/categories/getActiveForSelectedYear/" + currentYear)
            .then(r => r.json())
            .then(cats => {
                const expenseCats = cats.filter(c => c.level !== null && c.level >= 0);
                setCategories(expenseCats);
            });
    }, []);

    useEffect(() => {
        loadTopExpenses();
    }, [year, month, selectedCategories]);

    async function loadTopExpenses() {
        const response = await fetch("/budget/expenses/topExpensesForMonthAndCategory?year=" + year + "&month=" + month + "&categories=" + selectedCategories);
        const data = await response.json();
        setTopExpenses(data);
    }

    useEffect(() => {
        if (selectedCategories.length === 0) {
            setData({});
            return;
        }
        fetch("/budget/expenses/livingExpensesComparison", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(selectedCategories)
        })
            .then(r => r.json())
            .then(setData);
    }, [selectedCategories]);

    const years = Object.keys(data).map(Number).sort();

    const chartData = MONTHS_ARRAY.map(month => {
        const row = {monthName: getMonthName(month, "short"), month};
        years.forEach(year => {
            const found = (data[year] || []).find(d => d.month === month);
            row[year] = found ? Math.abs(found.amount) : 0;
        });
        return row;
    });

    function getAmount(year, month) {
        const found = (data[year] || []).find(d => d.month === month);
        return found ? Math.abs(found.amount) : 0;
    }

    function getYearTotal(year) {
        return (data[year] || []).reduce((sum, d) => sum + Math.abs(d.amount), 0);
    }

    function getMonthAvg(month) {
        if (years.length === 0) return 0;
        return years.reduce((sum, year) => sum + getAmount(year, month), 0) / years.length;
    }

    const body = (
        <Col sm={12}>
            <h2>Wydatki na życie – porównanie rok do roku</h2>

            <MonthYearFilter
                year={year}
                month={month}
                onYearChange={setYear}
                onMonthChange={setMonth}
            />

            <CategoryCheckboxRow
                categories={categories}
                selectedCategories={selectedCategories}
                setSelectedCategories={setSelectedCategories}
            />

            {years.length > 0 && (
                <>
                    <Row className="mt-3 mb-4">
                        <Col sm={12}>
                            <h5>Miesięczne wydatki rok do roku</h5>
                            <ResponsiveContainer width="100%" height={150}>
                                <BarChart data={chartData} margin={{top: 5, right: 30, left: 20, bottom: 5}}>
                                    <CartesianGrid strokeDasharray="3 3"/>
                                    <XAxis dataKey="monthName"/>
                                    <YAxis tickFormatter={v => formatNumber(v)}/>
                                    <Tooltip formatter={(value) => formatNumber(value)}/>
                                    <Legend/>
                                    {years.map((year, idx) => (
                                        <Bar key={year} dataKey={year}
                                             fill={monthColors[idx % monthColors.length]}/>
                                    ))}
                                </BarChart>
                            </ResponsiveContainer>
                        </Col>
                    </Row>
                    <Row>
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
                    <Row>
                        <Col sm={12}>
                            <h5>Zestawienie miesiąc do miesiąca</h5>
                            <Table responsive="sm" striped bordered size="sm">
                                <thead>
                                <tr className="table-info">
                                    <th>Miesiąc</th>
                                    {years.map(year => (
                                        <th key={year} style={{textAlign: "right"}}>{year}</th>
                                    ))}
                                    <th style={{textAlign: "right"}}>Srednia</th>
                                </tr>
                                </thead>
                                <tbody>
                                {MONTHS_ARRAY.map(month => {
                                    const avg = getMonthAvg(month);
                                    return (
                                        <tr key={month}>
                                            <td>{getMonthName(month, "long")}</td>
                                            {years.map(year => {
                                                const amount = getAmount(year, month);
                                                const isAbove = amount > 0 && avg > 0 && amount > avg * 1.1;
                                                const isBelow = amount > 0 && avg > 0 && amount < avg * 0.9;
                                                return (
                                                    <td key={year} style={{
                                                        textAlign: "right",
                                                        color: isAbove ? "#c0392b" : isBelow ? "#27ae60" : "inherit"
                                                    }}>
                                                        {amount > 0 ? formatNumber(amount) : "–"}
                                                    </td>
                                                );
                                            })}
                                            <td style={{textAlign: "right", fontWeight: "bold"}}>
                                                {formatNumber(avg)}
                                            </td>
                                        </tr>
                                    );
                                })}
                                </tbody>
                                <tfoot>
                                <tr className="table-secondary" style={{fontWeight: "bold"}}>
                                    <td>SUMA</td>
                                    {years.map(year => (
                                        <td key={year} style={{textAlign: "right"}}>
                                            {formatNumber(getYearTotal(year))}
                                        </td>
                                    ))}
                                    <td style={{textAlign: "right"}}>
                                        {formatNumber(years.reduce((s, y) => s + getYearTotal(y), 0) / (years.length || 1))}
                                    </td>
                                </tr>
                                </tfoot>
                            </Table>
                        </Col>
                    </Row>
                </>
            )}

            {years.length === 0 && selectedCategories.length > 0 && (
                <p className="text-muted mt-3">Brak danych dla wybranych kategorii.</p>
            )}
        </Col>
    );

    return <Main body={body}/>;
};

export default LivingExpensesView;
