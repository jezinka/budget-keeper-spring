import React from "react";
import {Line, LineChart, Tooltip} from "recharts";
import {formatNumber} from "../../Utils";
import {CURRENT_YEAR} from "./trendUtils";

const SPARKLINE_COLOR = "#1F78B4";

// full years plus the rolling last 12 months as the final point,
// so the incomplete current year doesn't look like a drop
const TrendSparkline = ({trend}) => {
    const data = Object.entries(trend.yearly)
        .map(([year, amount]) => ({label: year, amount}))
        .filter(d => Number(d.label) < CURRENT_YEAR);
    data.push({label: "ost. 12 mies.", amount: trend.last12});

    return (
        <LineChart width={120} height={32} data={data} margin={{top: 4, right: 4, bottom: 4, left: 4}}>
            <Line type="monotone" dataKey="amount" stroke={SPARKLINE_COLOR} strokeWidth={2}
                  dot={false} activeDot={{r: 4}} isAnimationActive={false}/>
            <Tooltip
                formatter={value => [formatNumber(value), ""]}
                labelFormatter={(_, payload) => payload[0]?.payload?.label}
                separator=""
                wrapperStyle={{zIndex: 10}}
                contentStyle={{padding: "2px 6px", fontSize: 12}}
            />
        </LineChart>
    );
};

export default TrendSparkline;
