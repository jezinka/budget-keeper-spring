import React from "react";
import {Cell, Legend, Pie, PieChart, ResponsiveContainer, Tooltip} from "recharts";
import {formatNumber} from "../../Utils";

const colors = {
    "Zaplanowane": "#198754",
    "Przekroczony plan": "#ffc107",
    "Niezaplanowane": "#dc3545"
};

const PlanPieChart = ({data}) => <ResponsiveContainer width="100%" height={220}>
    <PieChart><Pie data={data} dataKey="amount" nameKey="name" outerRadius={70} label>
        {data.map(entry => <Cell key={entry.name} fill={colors[entry.name]}/>)}
    </Pie><Tooltip formatter={formatNumber}/><Legend/></PieChart>
</ResponsiveContainer>;

export default PlanPieChart;
