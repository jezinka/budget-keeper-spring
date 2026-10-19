import React from "react";
import {Cell, Legend, Pie, PieChart, ResponsiveContainer, Tooltip} from "recharts";
import {formatNumber} from "../../Utils";

// Kwoty przychodzą już dodatnie (PieChartExpenseDto robi abs()).
const ExpensePieChart = ({data, hueStep = 36, legend = false}) => <ResponsiveContainer width="100%" height={300}>
    <PieChart>
        <Pie data={data} cx="50%" cy="50%" labelLine={false} outerRadius={80} dataKey="amount"
             label={({name, percent}) => `${name}: ${(percent * 100).toFixed(0)}%`}>
            {data.map((entry, index) => <Cell key={`cell-${index}`} fill={`hsl(${index * hueStep}, 70%, 50%)`}/>)}
        </Pie>
        <Tooltip formatter={formatNumber}/>
        {legend && <Legend/>}
    </PieChart>
</ResponsiveContainer>;

export default ExpensePieChart;
