import React, {useEffect, useMemo, useState} from "react";
import Main from "../main/Main";
import {
    Bar, BarChart, CartesianGrid, Legend, Tooltip, XAxis, YAxis, ResponsiveContainer
} from "recharts";
import {Col, Row, Form, Button} from "react-bootstrap";
import {handleError, monthColors, formatNumber} from "../../Utils";

const LifestyleInflation = () => {
    const [data, setData] = useState([]);
    const [years, setYears] = useState([]);
    const [topN, setTopN] = useState(10);
    const [aggregateOthers, setAggregateOthers] = useState(true);
    const [modeStacked, setModeStacked] = useState(false);

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        const response = await fetch('/budget/expenses/lifestyleInflation');
        if (response.ok) {
            let payload = await response.json();
            let sorted = payload.data.sort((a, b) => ('' + a.category).localeCompare(b.category));
            const yrs = [...new Set(sorted.map(k => Object.keys(k)).flat().filter(k => k !== 'category'))];
            setYears(yrs);
            setData(sorted);
            return;
        }
        handleError();
    }

    const aggregated = useMemo(() => {
        if (!data || data.length === 0) return [];

        // use full dataset (server now filters out salary/investment categories)
        const dataset = data;

        // by category (existing behavior) on dataset
        const totals = dataset.map(d => {
            const sum = years.reduce((s, y) => s + (Number(d[y]) || 0), 0);
            return {category: d.category, sum};
        }).sort((a, b) => b.sum - a.sum);

        const top = topN > 0 ? totals.slice(0, topN).map(t => t.category) : totals.map(t => t.category);
        // ensure topData is in the same order as totals (descending sum)
        const categoryMap = Object.fromEntries(dataset.map(d => [d.category, d]));
        const topData = top.map(cat => ({...(categoryMap[cat] || {category: cat})}));
        if (!aggregateOthers) return topData;

        const others = dataset.filter(d => !top.includes(d.category));
        if (others.length === 0) return topData;
        const otherRow = {category: "Inne"};
        years.forEach(y => {
            otherRow[y] = others.reduce((s, r) => s + (Number(r[y]) || 0), 0);
        });
        return [...topData, otherRow];
    }, [data, years, topN, aggregateOthers, modeStacked]);

    const chartData = useMemo(() => {
        return aggregated.map(d => {
            const out = {category: d.category};
            years.forEach(y => out[y] = Number(d[y]) || 0);
            return out;
        });
    }, [aggregated, years]);

    const totalsPerCategory = useMemo(() => {
        return chartData.map(d => ({
            category: d.category,
            total: years.reduce((s, y) => s + (Number(d[y]) || 0), 0)
        }));
    }, [chartData, years]);

    function tooltipFormatter(value, name, props) {
        const category = props.payload && props.payload.category;
        const idx = years.indexOf(name);
        let pctChange = null;
        if (idx > 0 && category) {
            const prevYear = years[idx - 1];
            const prevVal = (props.payload && props.payload[prevYear]) || 0;
            pctChange = prevVal ? ((Number(value) - Number(prevVal)) / Math.abs(prevVal)) * 100 : null;
        }
        const pctText = (pctChange == null) ? "" : ` (${pctChange >= 0 ? '+' : ''}${pctChange.toFixed(1)}%)`;
        return [`${formatNumber(value)}${pctText}`, name];
    }

    function renderBars() {
        return years.map((year, idx) => (
            <Bar key={year} dataKey={year} stackId={modeStacked ? "a" : undefined}
                 fill={monthColors[idx % monthColors.length]}/>
        ));
    }

    const controlRow = (
        <Row className="mb-2 align-items-center">
            <Col sm={3}>
                <Form.Select value={topN} onChange={(e) => setTopN(Number(e.target.value))}>
                    <option value={5}>Top 5</option>
                    <option value={10}>Top 10</option>
                    <option value={20}>Top 20</option>
                    <option value={0}>Wszystkie</option>
                </Form.Select>
            </Col>
            <Col sm={3}>
                <Form.Check type="checkbox" label="Agreguj pozostałe jako 'Inne'" checked={aggregateOthers}
                            onChange={e => setAggregateOthers(e.target.checked)}/>
            </Col>
            <Col sm={2}>
                <Form.Check type="checkbox" label="Stacked" checked={modeStacked}
                            onChange={e => setModeStacked(e.target.checked)}/>
            </Col>
            <Col sm={2} className="text-end">
                <Button variant="secondary" onClick={() => {
                    setTopN(10);
                    setAggregateOthers(true);
                    setModeStacked(false);
                }}>Reset</Button>
            </Col>
        </Row>
    );

    const table = (
        <div style={{marginTop: 12, overflowX: "auto"}}>
            <table className="table table-sm">
                <thead>
                <tr>
                    <th>Kategoria</th>
                    {years.map(y => <th key={y} style={{textAlign: "right"}}>{y}</th>)}
                    <th style={{textAlign: "right"}}>Razem</th>
                </tr>
                </thead>
                <tbody>
                {chartData.map(row => (
                    <tr key={row.category}>
                        <td>{row.category}</td>
                        {years.map(y => <td key={y} style={{textAlign: "right"}}>{formatNumber(row[y])}</td>)}
                        <td style={{textAlign: "right"}}>{formatNumber(totalsPerCategory.find(t => t.category === row.category).total)}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );

    const body = <>
        {controlRow}
        <Row>
            <Col>
                <div style={{width: "100%", height: 500}}>
                    <ResponsiveContainer>
                        <BarChart data={chartData} margin={{top: 20, right: 30, left: 20, bottom: 80}}>
                            <CartesianGrid strokeDasharray="3 3"/>
                            <XAxis dataKey="category" angle={-35} textAnchor="end" interval={0} height={70}/>
                            <YAxis/>
                            <Tooltip formatter={tooltipFormatter}/>
                            <Legend/>
                            {renderBars()}
                        </BarChart>
                    </ResponsiveContainer>
                </div>
            </Col>
        </Row>
        <Row>
            <Col>{table}</Col>
        </Row>
    </>

    return <Main body={body}/>
}

export default LifestyleInflation;
