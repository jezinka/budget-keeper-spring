import React, {useEffect, useState} from 'react';
import {
    CartesianGrid,
    Customized,
    Line,
    LineChart,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis,
} from 'recharts';
import {ButtonGroup, Button} from 'react-bootstrap';
import {formatNumber} from '../../Utils';

const GRANULARITIES = [
    {key: 'daily',   label: 'Dzień'},
    {key: 'weekly',  label: 'Tydzień'},
    {key: 'monthly', label: 'Miesiąc'},
    {key: 'yearly',  label: 'Rok'},
];

const getGroupKey = (dateStr, granularity) => {
    const [year, month, day] = dateStr.split('-');
    if (granularity === 'daily')   return dateStr;
    if (granularity === 'monthly') return `${year}-${month}`;
    if (granularity === 'yearly')  return year;
    if (granularity === 'weekly') {
        const d = new Date(dateStr);
        const dayOfWeek = (d.getDay() + 6) % 7;
        const monday = new Date(d);
        monday.setDate(d.getDate() - dayOfWeek);
        return monday.toISOString().slice(0, 10);
    }
    return dateStr;
};

const aggregate = (snapshots, granularity) => {
    if (granularity === 'daily') return snapshots;
    const map = new Map();
    for (const s of snapshots) {
        const key = getGroupKey(s.date, granularity);
        map.set(key, {
            date: key,
            value: Number(s.value),
            investedCapital: s.investedCapital != null ? Number(s.investedCapital) : null,
        });
    }
    return Array.from(map.values());
};

const formatLabel = (dateStr, granularity) => {
    if (!dateStr) return '';
    if (granularity === 'yearly') return dateStr;
    const [year, month, day] = dateStr.split('-');
    if (granularity === 'monthly') return `${month}.${year}`;
    return `${day}.${month}.${year}`;
};

const formatYAxis = (value) => {
    if (value >= 1000) return `${(value / 1000).toFixed(0)}k`;
    return value;
};

const formatThreshold = (value) => {
    if (value >= 1000000) return `${(value / 1000000).toFixed(1)}M`;
    if (value >= 1000) return `${(value / 1000).toFixed(0)}k`;
    return String(value);
};

const CustomTooltip = ({active, payload, label, granularity}) => {
    if (active && payload && payload.length) {
        return (
            <div style={{background: '#fff', border: '1px solid #ccc', padding: '8px 12px', borderRadius: 4}}>
                <p style={{margin: 0, fontWeight: 'bold'}}>{formatLabel(label, granularity)}</p>
                <p style={{margin: 0, color: '#4e79a7'}}>{formatNumber(payload[0].value)} PLN</p>
            </div>
        );
    }
    return null;
};

const FireCrossingDot = (props) => {
    const {cx, cy} = props;
    return (
        <g>
            <polygon
                points={`${cx},${cy - 7} ${cx + 6},${cy + 4} ${cx - 6},${cy + 4}`}
                fill="#198754"
                stroke="#fff"
                strokeWidth={1}
            />
        </g>
    );
};

const FireVerticalLines = ({xAxisMap, yAxisMap, crossingPoints}) => {
    if (!xAxisMap || !yAxisMap || !crossingPoints.length) return null;
    const xAxis = Object.values(xAxisMap)[0];
    const yAxis = Object.values(yAxisMap)[0];
    if (!xAxis?.scale || !yAxis?.scale) return null;

    const yBottom = yAxis.scale.range()[0];

    return (
        <g>
            {crossingPoints.map(({date, value, threshold}) => {
                const x = xAxis.scale(date);
                const y = yAxis.scale(value);
                if (x === undefined || isNaN(x)) return null;
                return (
                    <g key={date}>
                        <line
                            x1={x} y1={yBottom}
                            x2={x} y2={y}
                            stroke="#198754"
                            strokeDasharray="4 3"
                            strokeWidth={1.5}
                        />
                        <text
                            x={x + 3}
                            y={yBottom - 4}
                            fontSize={10}
                            fill="#198754"
                        >
                            {formatThreshold(threshold)}
                        </text>
                    </g>
                );
            })}
        </g>
    );
};

const PortfolioChart = ({fireStages = [], snapshots: snapshotsProp = null}) => {
    const [snapshots, setSnapshots] = useState(snapshotsProp || []);
    const [loading, setLoading] = useState(!snapshotsProp);
    const [granularity, setGranularity] = useState('monthly');

    useEffect(() => {
        if (snapshotsProp !== null) {
            setSnapshots(snapshotsProp);
            setLoading(false);
            return;
        }
        fetch('/budget/portfolio/snapshots')
            .then(res => res.json())
            .then(data => {
                setSnapshots(data);
                setLoading(false);
            })
            .catch(() => setLoading(false));
    }, [snapshotsProp]);

    if (loading) return <p>Ładowanie...</p>;
    if (snapshots.length === 0) return <p>Brak danych. Uruchom import snapshotów.</p>;

    const data = aggregate(snapshots, granularity);
    const values = data.map(s => Number(s.value));
    const minVal = Math.min(...values);
    const maxVal = Math.max(...values);
    const padding = (maxVal - minVal) * 0.1 || 1000;

    const crossingKeys = new Set(
        fireStages
            .filter(s => s.firstCrossedAt)
            .map(s => getGroupKey(s.firstCrossedAt, granularity))
    );

    const crossingPoints = fireStages
        .filter(s => s.firstCrossedAt)
        .map(s => {
            const groupKey = getGroupKey(s.firstCrossedAt, granularity);
            const point = data.find(d => d.date === groupKey);
            return point ? {date: groupKey, value: point.value, threshold: s.threshold} : null;
        })
        .filter(Boolean);

    const renderDot = (props) => {
        const {cx, cy, payload} = props;
        if (crossingKeys.has(payload.date)) {
            return <FireCrossingDot key={payload.date} cx={cx} cy={cy}/>;
        }
        if (granularity === 'daily') return null;
        return <circle key={payload.date} cx={cx} cy={cy} r={3} fill="#4e79a7" stroke="none"/>;
    };

    return (
        <>
            <ButtonGroup size="sm" className="mb-3">
                {GRANULARITIES.map(g => (
                    <Button
                        key={g.key}
                        variant={granularity === g.key ? 'primary' : 'outline-primary'}
                        onClick={() => setGranularity(g.key)}
                    >
                        {g.label}
                    </Button>
                ))}
            </ButtonGroup>

            <ResponsiveContainer width="100%" height={400}>
                <LineChart data={data} margin={{top: 20, right: 30, left: 10, bottom: 10}}>
                    <CartesianGrid strokeDasharray="3 3"/>
                    <XAxis
                        dataKey="date"
                        tickFormatter={(d) => formatLabel(d, granularity)}
                        tick={{fontSize: 12}}
                        interval="preserveStartEnd"
                    />
                    <YAxis
                        tickFormatter={formatYAxis}
                        domain={[Math.floor(minVal - padding), Math.ceil(maxVal + padding)]}
                        width={55}
                    />
                    <Tooltip content={<CustomTooltip granularity={granularity}/>}/>
                    <Customized component={(props) => (
                        <FireVerticalLines {...props} crossingPoints={crossingPoints}/>
                    )}/>
                    <Line
                        type="monotone"
                        dataKey="investedCapital"
                        stroke="#adb5bd"
                        strokeWidth={1.5}
                        strokeDasharray="5 3"
                        dot={false}
                        connectNulls={false}
                        name="Wkład własny"
                    />
                    <Line
                        type="monotone"
                        dataKey="value"
                        stroke="#4e79a7"
                        strokeWidth={2}
                        dot={renderDot}
                        activeDot={{r: 5}}
                        connectNulls={false}
                        name="Wartość portfela"
                    />
                </LineChart>
            </ResponsiveContainer>
        </>
    );
};

export default PortfolioChart;
