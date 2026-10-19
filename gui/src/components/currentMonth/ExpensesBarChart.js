import React from "react";
import {Bar, BarChart, LabelList, ResponsiveContainer, XAxis, YAxis} from "recharts";

const ExpensesBarChart = ({data}) => {

    return (
        <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data}>
            <YAxis width={30}/>
            <XAxis height={110} dataKey="category" interval={0}
                   tick={{angle: -90, textAnchor: 'end', 'dominantBaseline': 'ideographic'}}/>

            <Bar dataKey="amount" fill="#6BBDFF">
                <LabelList dataKey="amount" position="bottom" angle={270} offset={-100}/>
            </Bar>
        </BarChart>
        </ResponsiveContainer>
    );
}

export default ExpensesBarChart;