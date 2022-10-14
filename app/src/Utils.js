import React from "react";

export const SUM_MONTH = 99;
export const SUM_CATEGORY = 'SUMA';
export const EMPTY_OPTION = -1;

export const MONTHS_ARRAY = Array.from({length: 12}, (x, i) => (i + 1));
export const SUMMARY_STYLE = {backgroundColor: '#77e0be', fontWeight: 'bold'};

export function getDate() {
    return new Date().toISOString().split('T')[0];
}

export function getMonthName(monthNumber, format) {
    const date = new Date();
    date.setMonth(monthNumber - 1);

    return date.toLocaleString('pl-PL', {month: format});
}

export function handleError() {
    // TODO: show in log panel; global context??
    console.log("ERROR!");
}

export function addSumPerMonth(data) {
    data.forEach((record) => {
        const months = MONTHS_ARRAY.map(m => getMonthName(m, 'short'));
        record.sum = Object.keys(record).filter(k => months.includes(k))
            .map((d) => record[d])
            .reduce((a, b) => a + b, 0);
    })
}

export const renderCustomizedLabel = ({cx, cy, midAngle, innerRadius, outerRadius, percent, index}) => {
    const RADIAN = Math.PI / 180;
    const radius = innerRadius + (outerRadius - innerRadius) * 0.5;
    const x = cx + radius * Math.cos(-midAngle * RADIAN);
    const y = cy + radius * Math.sin(-midAngle * RADIAN);

    return (
        <text x={x} y={y} fill="white" textAnchor={x > cx ? 'start' : 'end'} dominantBaseline="central">
            {`${(percent * 100).toFixed(0)}%`}
        </text>
    );
};

export function getSumFromMap(list, key) {
    return list.map((d) => d[key]).reduce((a, b) => a + b, 0);
}