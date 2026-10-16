import React, {useEffect, useState} from "react";
import {Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis} from "recharts";
import {formatNumber, getMonthName, monthColors, MONTHS_ARRAY} from "../../Utils";

const CategoryMonthlyChart = ({categories}) => {
    const [data, setData] = useState({});

    useEffect(() => {
        fetch("/budget/expenses/livingExpensesComparison", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(categories)
        })
            .then(r => r.json())
            .then(setData);
    }, [categories.join(",")]);

    const years = Object.keys(data).map(Number).sort();

    const chartData = MONTHS_ARRAY.map(month => {
        const row = {monthName: getMonthName(month, "short")};
        years.forEach(year => {
            const found = (data[year] || []).find(d => d.month === month);
            row[year] = found ? Math.abs(found.amount) : 0;
        });
        return row;
    });

    if (years.length === 0) {
        return <p className="text-muted">Ładowanie…</p>;
    }

    return (
        <ResponsiveContainer width="100%" height={220}>
            <BarChart data={chartData} margin={{top: 5, right: 30, left: 20, bottom: 5}}>
                <CartesianGrid strokeDasharray="3 3" vertical={false}/>
                <XAxis dataKey="monthName"/>
                <YAxis tickFormatter={v => formatNumber(v)}/>
                <Tooltip formatter={value => formatNumber(value)}/>
                <Legend/>
                {years.map((year, idx) => (
                    <Bar key={year} dataKey={year} fill={monthColors[idx % monthColors.length]}/>
                ))}
            </BarChart>
        </ResponsiveContainer>
    );
};

export default CategoryMonthlyChart;
