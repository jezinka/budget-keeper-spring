import React, {useMemo} from 'react';
import {Bar, BarChart, CartesianGrid, Cell, ReferenceLine, ResponsiveContainer, Tooltip, XAxis, YAxis,} from 'recharts';
import {formatNumber} from '../../Utils';

const formatLabel = (dateStr) => {
    if (!dateStr) return '';
    const [year, month] = dateStr.split('-');
    return `${month}.${year}`;
};

const CustomTooltip = ({active, payload, label}) => {
    if (!active || !payload || !payload.length) return null;
    const d = payload[0].payload;
    const isLoss = d.profit < 0;
    return (
        <div style={{
            background: '#fff',
            border: '1px solid #ccc',
            padding: '8px 12px',
            borderRadius: 4,
            fontSize: '0.8rem'
        }}>
            <p style={{margin: 0, fontWeight: 'bold'}}>{formatLabel(label)}</p>
            {isLoss ? (
                <>
                    <p style={{margin: 0, color: '#4e79a7'}}>Wartość: {formatNumber(Math.round(d.value))} PLN
                        ({d.pctInvested.toFixed(1)}%)</p>
                    <p style={{margin: 0, color: '#dc3545'}}>Strata: {formatNumber(Math.round(Math.abs(d.profit)))} PLN
                        ({d.pctProfit.toFixed(1)}%)</p>
                </>
            ) : (
                <>
                    <p style={{margin: 0, color: '#4e79a7'}}>Wkład: {formatNumber(Math.round(d.investedCapital))} PLN
                        ({d.pctInvested.toFixed(1)}%)</p>
                    <p style={{margin: 0, color: '#10d57a'}}>Zysk: {formatNumber(Math.round(d.profit))} PLN
                        ({d.pctProfit.toFixed(1)}%)</p>
                </>
            )}
        </div>
    );
};

const PortfolioCompositionChart = ({snapshots}) => {
    const data = useMemo(() => {
        if (!snapshots || snapshots.length === 0) return [];

        const monthly = new Map();
        for (const s of snapshots) {
            if (s.value == null || s.investedCapital == null) continue;
            const ym = s.date.slice(0, 7);
            monthly.set(ym, {
                date: ym,
                value: Number(s.value),
                investedCapital: Number(s.investedCapital),
            });
        }

        return Array.from(monthly.values())
            .sort((a, b) => a.date.localeCompare(b.date))
            .map(d => {
                const profit = d.value - d.investedCapital;
                // Zysk: base=value → wkład% + zysk% = 100%
                // Strata: base=investedCapital → obecnaWartość% + strata% = 100%
                const base = profit >= 0 ? d.value : d.investedCapital;
                const pctInvested = Math.round(((profit >= 0 ? d.investedCapital : d.value) / base) * 10000) / 100;
                const pctProfit = 100 - pctInvested;
                return {...d, profit, pctInvested, pctProfit};
            })
            .filter(d => d.value > 0);
    }, [snapshots]);

    if (!data.length) return null;

    const last = data[data.length - 1];

    return (
        <div>
            <h6 className="text-muted">Struktura portfela: wkład własny vs zysk</h6>
            <div className="d-flex gap-3 mb-2" style={{fontSize: '0.8rem'}}>
                <span style={{color: '#4e79a7'}}>■ Wkład własny</span>
                <span style={{color: '#10d57a'}}>■ Zysk</span>
                {last.pctProfit >= 0 && (
                    <span className="text-muted ms-2">
                        Aktualnie: {last.pctInvested.toFixed(1)}% wkład / {last.pctProfit.toFixed(1)}% zysk
                    </span>
                )}
            </div>
            <ResponsiveContainer width="100%" height={300}>
                <BarChart data={data} margin={{top: 10, right: 20, left: 10, bottom: 10}}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false}/>
                    <XAxis
                        dataKey="date"
                        tickFormatter={formatLabel}
                        tick={{fontSize: 11}}
                        interval="preserveStartEnd"
                    />
                    <YAxis
                        domain={[0, 100]}
                        width={40}
                        tick={{fontSize: 11}}
                    />
                    <ReferenceLine x={new Date('2024-07-26')} stroke="red" label="Max PV PAGE" stroke="var(--color-chart-1)"
                                   strokeDasharray="5 5"/>
                    <Tooltip content={<CustomTooltip/>}/>
                    <Bar dataKey="pctProfit" stackId="a" fill="#198754" name="Zysk" isAnimationActive={false}
                         radius={[3, 3, 0, 0]}>
                        {data.map((entry, index) => (
                            <Cell key={index} fill={entry.pctProfit >= 0 ? '#10d57a' : '#dc3545'}
                                  fillOpacity={index === data.length - 1 ? 1 : 0.7}/>
                        ))}
                    </Bar>
                    <Bar dataKey="pctInvested" stackId="a" fill="#4e79a7" name="Wkład własny" isAnimationActive={false}>
                        {data.map((entry, index) => (
                            <Cell key={index} fill="#4e79a7" fillOpacity={index === data.length - 1 ? 1 : 0.7}/>
                        ))}
                    </Bar>
                </BarChart>
            </ResponsiveContainer>
        </div>
    );
};

export default PortfolioCompositionChart;
