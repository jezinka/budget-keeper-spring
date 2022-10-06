export const SUM_MONTH = 99;
export const SUM_CATEGORY = 'SUMA';

export const MONTHS_ARRAY = Array.from({length: 12}, (x, i) => (i + 1));
export const SUMMARY_STYLE = {backgroundColor: '#77e0be', fontWeight: 'bold'};

export function getMonthName(monthNumber) {
    const date = new Date();
    date.setMonth(monthNumber - 1);

    return date.toLocaleString('pl-PL', {month: 'long'});
}
