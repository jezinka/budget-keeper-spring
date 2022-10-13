import React from "react";
import {LabelList, Pie, PieChart} from "recharts";

const ExpensesPieChart = ({data}) => {

    const RADIAN = Math.PI / 180;
    const renderCustomizedLabel = ({cx, cy, midAngle, innerRadius, outerRadius, percent, index}) => {
        const radius = innerRadius + (outerRadius - innerRadius) * 0.5;
        const x = cx + radius * Math.cos(-midAngle * RADIAN);
        const y = cy + radius * Math.sin(-midAngle * RADIAN);

        return (
            <text x={x} y={y} fill="white" textAnchor={x > cx ? 'start' : 'end'} dominantBaseline="central">
                {`${(percent * 100).toFixed(0)}%`}
            </text>
        );
    };

    const prepareData = () => {

        function getSumFromMap(list) {
            return list.map((d) => d.amount).reduce((a, b) => a + b, 0);
        }

        const sum = getSumFromMap(data);
        const filteredData = data.filter((d) => {
            return d.amount / sum >= 0.03
        });

        filteredData.push({category: 'inne', amount: sum - getSumFromMap(filteredData)});
        return filteredData;
    }

    return (
        <PieChart width={400} height={300}>
            <Pie
                data={prepareData()}
                cx="50%"
                cy="50%"
                outerRadius={120}
                dataKey="amount"
                fill="#6BBDFF"
                label={renderCustomizedLabel}>
                <LabelList dataKey="category" position="outside" stroke={"black"} offset={20}/>
            </Pie>
        </PieChart>
    )
}

export default ExpensesPieChart;