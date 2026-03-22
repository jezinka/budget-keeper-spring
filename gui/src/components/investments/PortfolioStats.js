import React from 'react';
import {formatNumber} from '../../Utils';

const Stat = ({label, value, sub, color}) => (
    <div style={{minWidth: 110}}>
        <div style={{fontSize: '0.7rem', color: '#6c757d', textTransform: 'uppercase', letterSpacing: '0.04em'}}>
            {label}
        </div>
        <div style={{fontSize: '1.05rem', fontWeight: 600, color: color || '#212529'}}>
            {value}
        </div>
        {sub && <div style={{fontSize: '0.7rem', color: '#adb5bd'}}>{sub}</div>}
    </div>
);

const Divider = () => (
    <div style={{width: 1, background: '#dee2e6', margin: '0 12px', alignSelf: 'stretch'}}/>
);

const formatDate = (dateStr) => {
    if (!dateStr) return '—';
    const [year, month, day] = dateStr.split('-');
    return `${day}.${month}.${String(year).slice(2)}`;
};

const formatPct = (n) => {
    if (n == null) return '—';
    const sign = n >= 0 ? '+' : '';
    return `${sign}${n.toFixed(1)}%`;
};

const PortfolioStats = ({snapshots}) => {
    if (!snapshots || snapshots.length < 2) return null;

    const withValue = snapshots.filter(s => s.value != null);
    const current = Number(withValue[withValue.length - 1].value);
    const currentInvested = Number(withValue[withValue.length - 1].investedCapital);
    const profit = currentInvested > 0 ? current - currentInvested : null;
    const roi = profit != null && currentInvested > 0 ? (profit / currentInvested) * 100 : null;

    const ath = Math.max(...withValue.map(s => Number(s.value)));
    const athDate = withValue.find(s => Number(s.value) === ath)?.date;
    const drawdown = ath > 0 ? ((current - ath) / ath) * 100 : 0;

    // Monthly returns
    const monthly = {};
    for (const s of withValue) {
        const ym = s.date.slice(0, 7);
        monthly[ym] = Number(s.value);
    }
    const months = Object.keys(monthly).sort();
    const returns = months.slice(1).map((m, i) => ({
        month: m,
        ret: ((monthly[m] - monthly[months[i]]) / monthly[months[i]]) * 100,
    }));
    const best = returns.length ? returns.reduce((a, b) => b.ret > a.ret ? b : a) : null;
    const worst = returns.length ? returns.reduce((a, b) => b.ret < a.ret ? b : a) : null;

    return (
        <div className="d-flex flex-wrap gap-3 align-items-center py-2 px-1 mb-2"
             style={{borderBottom: '1px solid #dee2e6'}}>
            <Stat
                label="Wartość"
                value={`${formatNumber(current)} PLN`}
            />
            {profit != null && (
                <>
                    <Divider/>
                    <Stat
                        label="Zysk"
                        value={`${formatNumber(Math.round(profit))} PLN`}
                        sub={`ROI ${formatPct(roi)}`}
                        color={profit >= 0 ? '#198754' : '#dc3545'}
                    />
                </>
            )}
            <Divider/>
            <Stat
                label="ATH"
                value={`${formatNumber(ath)} PLN`}
                sub={formatDate(athDate)}
            />
            <Divider/>
            <Stat
                label="Drawdown"
                value={formatPct(drawdown)}
                color={drawdown < -5 ? '#dc3545' : drawdown < -2 ? '#fd7e14' : '#6c757d'}
            />
            {best && <>
                <Divider/>
                <Stat
                    label="Najlepszy mies."
                    value={formatPct(best.ret)}
                    sub={best.month}
                    color="#198754"
                />
            </>}
            {worst && <>
                <Divider/>
                <Stat
                    label="Najgorszy mies."
                    value={formatPct(worst.ret)}
                    sub={worst.month}
                    color="#dc3545"
                />
            </>}
        </div>
    );
};

export default PortfolioStats;
