import React, {useEffect, useState} from "react";
import {Bar, BarChart, LabelList, XAxis, YAxis} from "recharts";

const DailyBarChart = () => {
    const [data, setData] = useState([]);

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        const response = await fetch('/expenses/dailyExpenses')
        const data = await response.json();
        setData(data);
    }

    // last day of current month
    const lastDay = new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0).getDate();

    return (
        <BarChart width={500} height={500} data={data} layout="vertical" className={"mt-3"}>
            <XAxis type="number" domain={[0, 500]}/>

            <YAxis dataKey="day" type="number" domain={[1, lastDay]} interval={0} tickCount={lastDay + 1}/>
            <Bar dataKey="amount" fill="#6BBDFF">
                <LabelList dataKey="amount" position="right"/>
            </Bar>
        </BarChart>
    );
}

export default DailyBarChart;