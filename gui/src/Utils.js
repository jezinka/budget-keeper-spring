export const SUM_MONTH = 99;
export const SUM_CATEGORY = 'SUMA';
export const EMPTY_OPTION = -1;
export const FIRST_YEAR = 2021;
export const UNKNOWN_CATEGORY = -999;

export const MONTHS_ARRAY = Array.from({length: 12}, (x, i) => (i + 1));
export const DATE_TIME_FORMAT = new Intl.DateTimeFormat('pl-PL', {dateStyle: 'medium', timeStyle: 'medium'});

export function getMonthName(monthNumber, format) {
    const date = new Date(new Date().getFullYear(), monthNumber - 1, 1);
    return date.toLocaleString('pl-PL', {month: format});
}

export function handleError() {
    // TODO: show in log panel; global context??
    console.log("ERROR!");
}

export function formatNumber(number) {
    let numberFormat = new Intl.NumberFormat('pl-PL', {maximumFractionDigits: 2, minimumFractionDigits: 2});
    return numberFormat.format(number);
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
    return ["pon.", "wt.", "śr.", "czw.", "pt.", "sob.", "niedz."];
}

export const monthColors = [
    '#A6CEE3', '#1F78B4', '#b413b9', '#33A02C', '#FB9A99', '#E31A1C',
    '#FDBF6F', '#FF7F00', '#CAB2D6', '#6A3D9A', '#16AC1C', '#B15928']

export const categoryLevelColors = {
    "0": {background: "#d4edda", chart: "#28a745"},
    "1": {background: "#fff3cd", chart: "#ffc107"},
    "3": {background: "#ffe4cc", chart: "#fd7e14"},
    "2": {background: "#e4d9f3", chart: "#9b59b6"},
    "4": {background: "#ffeaa7", chart: "#f39c12"},
    "5": {background: "#ffa7d6", chart: "#f312e0"},
    "6": {background: "#a7fff5", chart: "#12ecf3"}
};