import {Col} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import {Bar, BarChart, Tooltip, XAxis, YAxis} from "recharts";
import {addSumPerMonth, getMonthName, handleError, MONTHS_ARRAY} from "../../Utils";
import YearFilter from "./YearFilter";
import SpinnerLoadButton from "./SpinnerLoadButton";

const YearAtTheGlanceBarChart = () => {
    const [data, setData] = useState([])
    const [formState, setFormState] = useState({year: new Date().getFullYear()});

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        const response = await fetch('/groupedExpenses/getPivot', {
            method: "POST",
            body: JSON.stringify(formState),
            headers: {'Content-Type': 'application/json'}
        })
        if (response.ok) {
            let data = await response.json();

            addSumPerMonth(data);
            data = data.filter((d) => d.sum < 0);
            return setData(data.sort((a, b) => a.sum - b.sum));
        }
        handleError();
    }

    let body = <>
        <YearFilter formState={formState} formHandler={setFormState}/>
        <SpinnerLoadButton loadData={loadData}/>
        <Col>
            <BarChart width={1700} height={800} data={data}>
                <Tooltip/>
                <YAxis width={40}/>
                <XAxis height={110}
                       dataKey="category"
                       interval={0}
                       tick={{angle: -90, textAnchor: 'end', 'dominantBaseline': 'ideographic'}}/>
                {MONTHS_ARRAY.map(month =>
                    <Bar
                        dataKey={getMonthName(month, 'short')}
                        stackId="a"
                        fill={MONTHS_ARRAY.indexOf(month) % 2 === 0 ? "#8884d8" : "#82ca9d"}/>
                )}
            </BarChart>
        </Col>
    </>;

    return <Main body={body}/>
}

export default YearAtTheGlanceBarChart;