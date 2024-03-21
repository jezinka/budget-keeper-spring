import React from "react";

export const SUM_MONTH = 99;
export const SUM_CATEGORY = 'SUMA';
export const EMPTY_OPTION = -1;

export const MONTHS_ARRAY = Array.from({length: 12}, (x, i) => (i + 1));
export const DATE_TIME_FORMAT = new Intl.DateTimeFormat('pl-PL', {dateStyle: 'medium', timeStyle: 'medium'});
export const DATE_FORMAT = new Intl.DateTimeFormat('pl-PL', {dateStyle: 'short'});

export function getDate() {
    return new Date().toISOString().split('T')[0];
}

export function getMonthName(monthNumber, format) {
    const date = new Date(new Date().getFullYear(), monthNumber - 1, 1);
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

export function formatNumber(number) {
    let numberFormat = new Intl.NumberFormat('pl-PL', {maximumFractionDigits: 2, minimumFractionDigits: 2});
    return numberFormat.format(number);
}

export const renderCustomizedLabel = ({cx, cy, midAngle, innerRadius, outerRadius, percent, index}) => {
    const RADIAN = Math.PI / 180;
    const radius = innerRadius + (outerRadius - innerRadius) * 0.5;
    const x = cx + radius * Math.cos(-midAngle * RADIAN);
    const y = cy + radius * Math.sin(-midAngle * RADIAN);

    return (<text x={x} y={y} fill="white" textAnchor={x > cx ? 'start' : 'end'} dominantBaseline="central">
        {`${(percent * 100).toFixed(0)}%`}
    </text>);
};

export function getSumFromMap(list, key) {
    return list.map((d) => d[key]).reduce((a, b) => a + b, 0);
}

export function getFirstDayOfCurrentMonth() {
    const date = new Date();

    const firstDay = new Date(
        date.getFullYear(),
        date.getMonth(),
        1
    );
    return new Intl.DateTimeFormat('sv-SE').format(firstDay);
}

export function getDaysOfWeek() {
    const {format} = new Intl.DateTimeFormat("pl-PL", {weekday: "short"});
    return [...Array(7).keys()]
        .map((day) => format(new Date(Date.UTC(new Date().getFullYear(), 0, day + 1))));
}