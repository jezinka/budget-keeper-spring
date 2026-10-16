import {formatNumber} from "../../Utils";

export const CHANGE_THRESHOLD = 0.1;
export const UP_COLOR = "#c0392b";
export const DOWN_COLOR = "#27ae60";
export const CURRENT_YEAR = new Date().getFullYear();

export function formatDiff(value) {
    return (value > 0 ? "+" : "") + formatNumber(value);
}

export function formatPercent(value) {
    if (value === null || value === undefined) return "–";
    return (value > 0 ? "+" : "") + (value * 100).toFixed(0) + "%";
}

export function changeColor(percent) {
    if (percent === null || percent === undefined) return "inherit";
    if (percent > CHANGE_THRESHOLD) return UP_COLOR;
    if (percent < -CHANGE_THRESHOLD) return DOWN_COLOR;
    return "inherit";
}

// the same rule as the backend: CAGR over full years with non-zero spending
export function calculateCagr(yearly) {
    const fullYears = Object.entries(yearly)
        .map(([year, amount]) => [Number(year), amount])
        .filter(([year, amount]) => year < CURRENT_YEAR && amount > 0)
        .sort((a, b) => a[0] - b[0]);
    if (fullYears.length < 2) return null;
    const [firstYear, firstAmount] = fullYears[0];
    const [lastYear, lastAmount] = fullYears[fullYears.length - 1];
    return Math.pow(lastAmount / firstAmount, 1 / (lastYear - firstYear)) - 1;
}

export function groupTrendsByLevel(trends, levels) {
    const byLevel = {};
    trends.forEach(t => {
        const group = byLevel[t.level] ||= {
            category: levels.find(l => Number(l.level) === t.level)?.name || "Poziom " + t.level,
            level: t.level,
            categories: [],
            yearly: {},
            last12: 0,
            previous12: 0
        };
        group.categories.push(t.category);
        group.last12 += t.last12;
        group.previous12 += t.previous12;
        Object.entries(t.yearly).forEach(([year, amount]) => {
            group.yearly[year] = (group.yearly[year] || 0) + amount;
        });
    });
    return Object.values(byLevel).map(g => ({
        ...g,
        diff: g.last12 - g.previous12,
        diffPercent: g.previous12 === 0 ? null : (g.last12 - g.previous12) / g.previous12,
        cagr: calculateCagr(g.yearly)
    }));
}
