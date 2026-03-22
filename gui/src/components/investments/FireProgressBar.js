import React from 'react';
import {formatNumber} from '../../Utils';

const W = 900;
const PAD = 30;
const BAR_W = W - PAD * 2;
const BAR_Y = 35;
const BAR_H = 6;
const TICK_ABOVE = BAR_Y - 4;
const TICK_BELOW = BAR_Y + BAR_H + 4;

const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const [year, month, day] = dateStr.split('-');
    return `${day}.${month}.${String(year).slice(2)}`;
};

const toX = (value, max) => PAD + Math.min((Number(value) / max) * BAR_W, BAR_W);

const FireProgressBar = ({fireStages = [], currentValue}) => {
    if (!fireStages.length) return null;

    const maxThreshold = Math.max(...fireStages.map(s => Number(s.threshold)));
    const currentX = currentValue ? toX(currentValue, maxThreshold) : null;
    const crossed = fireStages.filter(s => s.firstCrossedAt);
    const pct = currentValue ? ((Number(currentValue) / maxThreshold) * 100).toFixed(1) : null;

    return (
        <div className="mt-4">
            <div className="d-flex justify-content-between align-items-baseline mb-1">
                <small className="text-muted">Etapy FIRE</small>
                {pct && (
                    <small className="text-muted" style={{fontSize: '0.72rem'}}>
                        {formatNumber(currentValue)} PLN &nbsp;·&nbsp; {pct}% celu
                        &nbsp;·&nbsp; {crossed.length} / {fireStages.length}
                    </small>
                )}
            </div>

            <svg viewBox={`0 0 ${W} 80`} style={{width: '100%', overflow: 'visible'}}>
                {/* Background track */}
                <rect x={PAD} y={BAR_Y} width={BAR_W} height={BAR_H} rx={3} fill="#f1f3f5"/>

                {/* Progress fill */}
                {currentX !== null && (
                    <rect x={PAD} y={BAR_Y} width={currentX - PAD} height={BAR_H} rx={3} fill="#a8d5b5"/>
                )}

                {/* Stage ticks */}
                {fireStages.map((stage, i) => {
                    const x = toX(stage.threshold, maxThreshold);
                    const isCrossed = !!stage.firstCrossedAt;
                    const pctLabel = Math.round((Number(stage.threshold) / maxThreshold) * 100) + '%';
                    const isAbove = i % 2 === 0;

                    return (
                        <g key={stage.id}>
                            <line
                                x1={x} y1={TICK_ABOVE}
                                x2={x} y2={TICK_BELOW}
                                stroke={isCrossed ? '#6aab85' : '#dee2e6'}
                                strokeWidth={1}
                            />
                            {isCrossed ? (
                                <>
                                    <text
                                        x={x} y={isAbove ? BAR_Y - 8 : TICK_BELOW + 12}
                                        textAnchor="middle" fontSize={8} fill="#6aab85"
                                    >
                                        {formatDate(stage.firstCrossedAt)}
                                    </text>
                                    <text
                                        x={x} y={isAbove ? BAR_Y - 18 : TICK_BELOW + 22}
                                        textAnchor="middle" fontSize={7} fill="#b0c8ba"
                                    >
                                        {pctLabel}
                                    </text>
                                </>
                            ) : (
                                <text
                                    x={x} y={isAbove ? BAR_Y - 8 : TICK_BELOW + 12}
                                    textAnchor="middle" fontSize={7} fill="#dee2e6"
                                >
                                    {pctLabel}
                                </text>
                            )}
                        </g>
                    );
                })}

                {/* Current value marker */}
                {currentX !== null && (
                    <circle
                        cx={currentX} cy={BAR_Y + BAR_H / 2}
                        r={5} fill="#6aab85" stroke="#fff" strokeWidth={1.5}
                    />
                )}
            </svg>
        </div>
    );
};

export default FireProgressBar;
