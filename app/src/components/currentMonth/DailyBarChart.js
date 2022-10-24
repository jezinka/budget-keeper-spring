import React, {useEffect, useState} from "react";
import {Bar, BarChart, LabelList, XAxis, YAxis} from "recharts";

const DailyBarChart = () => {
    const [data, setData] = useState([]);

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        const response = await fetch('/groupedExpenses/dailyExpenses')
        const data = await response.json();
        setData(data);
    }


    return (
        <BarChart width={500} height={500} data={data} layout="vertical" className={"mt-3"}>
            <XAxis type="number" domain={[0, 1000]}/>
            <YAxis dataKey="day" type="number" domain={[1, 31]} interval={0} tickCount={32}/>
            <Bar dataKey="amount" fill="#6BBDFF">
                <LabelList dataKey="amount" position="right"/>
            </Bar>
        </BarChart>
    );
}

export default DailyBarChart;