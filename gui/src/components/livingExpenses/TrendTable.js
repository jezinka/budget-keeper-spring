import React, {useState} from "react";
import {Table} from "react-bootstrap";
import {formatNumber} from "../../Utils";
import TrendSparkline from "./TrendSparkline";
import CategoryMonthlyChart from "./CategoryMonthlyChart";
import {changeColor, formatDiff, formatPercent} from "./trendUtils";

const COLUMNS = [
    {key: "category", label: "Kategoria", align: "left"},
    {key: null, label: "Trend", align: "left"},
    {key: "last12", label: "Ost. 12 mies.", align: "right"},
    {key: "previous12", label: "Poprzednie 12 mies.", align: "right"},
    {key: "diff", label: "Δ zł", align: "right"},
    {key: "diffPercent", label: "Δ %", align: "right"},
    {key: "cagr", label: "Śr. wzrost roczny", align: "right"},
];

function compare(a, b, key) {
    const x = a[key], y = b[key];
    if (x === null || x === undefined) return 1;
    if (y === null || y === undefined) return -1;
    return typeof x === "string" ? x.localeCompare(y) : x - y;
}

const TrendTable = ({trends}) => {
    const [sort, setSort] = useState({key: "diff", desc: true});
    const [expanded, setExpanded] = useState(null);

    const sorted = [...trends].sort((a, b) => {
        const result = compare(a, b, sort.key);
        // nulls always last, regardless of direction
        if (a[sort.key] === null || a[sort.key] === undefined || b[sort.key] === null || b[sort.key] === undefined) {
            return result;
        }
        return sort.desc ? -result : result;
    });

    function toggleSort(key) {
        if (!key) return;
        setSort(prev => ({key, desc: prev.key === key ? !prev.desc : key !== "category"}));
    }

    return (
        <Table responsive striped bordered hover size="sm">
            <thead>
            <tr className="table-info">
                {COLUMNS.map(col => (
                    <th key={col.label}
                        style={{textAlign: col.align, cursor: col.key ? "pointer" : "default", whiteSpace: "nowrap"}}
                        onClick={() => toggleSort(col.key)}>
                        {col.label}{sort.key === col.key && (sort.desc ? " ▼" : " ▲")}
                    </th>
                ))}
            </tr>
            </thead>
            <tbody>
            {sorted.map(t => (
                <React.Fragment key={t.category}>
                    <tr style={{cursor: "pointer"}}
                        onClick={() => setExpanded(expanded === t.category ? null : t.category)}>
                        <td>{expanded === t.category ? "▾ " : "▸ "}{t.category}</td>
                        <td><TrendSparkline trend={t}/></td>
                        <td style={{textAlign: "right"}}>{formatNumber(t.last12)}</td>
                        <td style={{textAlign: "right"}}>{formatNumber(t.previous12)}</td>
                        <td style={{textAlign: "right", color: changeColor(t.diffPercent), fontWeight: "bold"}}>
                            {formatDiff(t.diff)}
                        </td>
                        <td style={{textAlign: "right", color: changeColor(t.diffPercent)}}>
                            {formatPercent(t.diffPercent)}
                        </td>
                        <td style={{textAlign: "right", color: changeColor(t.cagr)}}>
                            {formatPercent(t.cagr)}
                        </td>
                    </tr>
                    {expanded === t.category && (
                        <tr>
                            <td colSpan={COLUMNS.length}>
                                <CategoryMonthlyChart categories={t.categories || [t.category]}/>
                            </td>
                        </tr>
                    )}
                </React.Fragment>
            ))}
            </tbody>
        </Table>
    );
};

export default TrendTable;
