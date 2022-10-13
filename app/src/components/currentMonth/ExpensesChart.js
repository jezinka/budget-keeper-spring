import React, {useEffect, useState} from "react";
import {Bar, BarChart, LabelList, XAxis, YAxis} from "recharts";

const ExpensesChart = () => {
    const [data, setData] = useState([])

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        const response = await fetch('/groupedExpenses/currentMonthByCategory')
        const data = await response.json();
        setData(data);
    }

    return (
        <BarChart width={500} height={300} data={data}>
            <YAxis width={30} reversed={true}/>
            <XAxis height={110} dataKey="category" interval={0}
                   tick={{angle: -90, textAnchor: 'end', 'dominantBaseline': 'ideographic'}}/>

            <Bar dataKey="amount" fill="#8884d8">
                <LabelList dataKey="amount" position="bottom" angle={270} offset={-100}/>
            </Bar>
        </BarChart>
    );
}

export default ExpensesChart;