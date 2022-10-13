export const SUM_MONTH = 99;
export const SUM_CATEGORY = 'SUMA';
export const EMPTY_OPTION = -1;

export const MONTHS_ARRAY = Array.from({length: 12}, (x, i) => (i + 1));
export const SUMMARY_STYLE = {backgroundColor: '#77e0be', fontWeight: 'bold'};

export function getDate() {
    return new Date().toISOString().split('T')[0];
}

export function getMonthName(monthNumber) {
    const date = new Date();
    date.setMonth(monthNumber - 1);

    return date.toLocaleString('pl-PL', {month: 'long'});
}

export function handleError() {
    // TODO: show in log panel; global context??
    console.log("ERROR!");
}