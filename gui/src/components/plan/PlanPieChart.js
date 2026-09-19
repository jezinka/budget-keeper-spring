import React from "react";
import {Cell, Legend, Pie, PieChart, ResponsiveContainer, Tooltip} from "recharts";
import {formatNumber} from "../../Utils";

const PlanPieChart = ({data}) => <ResponsiveContainer width="100%" height={220}>
    <PieChart><Pie data={data} dataKey="amount" nameKey="name" outerRadius={70} label>
        {data.map((entry, index) => <Cell key={entry.name} fill={index ? "#dc3545" : "#198754"}/>)}
    </Pie><Tooltip formatter={formatNumber}/><Legend/></PieChart>
</ResponsiveContainer>;

export default PlanPieChart;
