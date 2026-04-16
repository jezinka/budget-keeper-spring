import React, {useEffect, useState} from "react";
import {Col, Row, Table} from "react-bootstrap";
import {formatNumber, getMonthName, monthColors, MONTHS_ARRAY} from "../../Utils";
import {Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis} from "recharts";
import Main from "../main/Main";
import CategoryCheckboxRow from "../year/CategoryCheckboxRow";

const LivingExpensesView = () => {
    const [categories, setCategories] = useState([]);
    const [selectedCategories, setSelectedCategories] = useState([]);
    const [data, setData] = useState({});

    useEffect(() => {
        const currentYear = new Date().getFullYear();
        fetch("/budget/categories/getActiveForSelectedYear/" + currentYear)
            .then(r => r.json())
            .then(cats => {
                const expenseCats = cats.filter(c => c.level !== null && c.level >= 0);
                setCategories(expenseCats);
                setSelectedCategories(expenseCats.map(c => c.name));
            });
    }, []);

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
                            <ResponsiveContainer width="100%" height={350}>
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
