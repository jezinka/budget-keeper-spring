import {Col} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import {Bar, BarChart, Tooltip, XAxis, YAxis} from "recharts";
import {addSumPerMonth, getMonthName, MONTHS_ARRAY} from "../../Utils";

const YearAtTheGlanceBarChart = () => {
    const [data, setData] = useState([])

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        const response = await fetch('/groupedExpenses/getPivot')
        let data = await response.json();

        addSumPerMonth(data);
        data = data.filter((d) => d.sum < 0);
        setData(data.sort((a, b) => a.sum - b.sum));
    }

    function getColor(month) {
        return MONTHS_ARRAY.indexOf(month) % 2 === 0 ? "#8884d8" : "#82ca9d";
    }

    let body = <>
        <Col>
            <BarChart width={1700} height={800} data={data}>
                <Tooltip/>
                <YAxis width={40}/>
                <XAxis height={110} dataKey="category" interval={0}
                       tick={{angle: -90, textAnchor: 'end', 'dominantBaseline': 'ideographic'}}/>
                {MONTHS_ARRAY.map(month =>
                    <Bar dataKey={getMonthName(month, 'short')} stackId="a"
                         fill={getColor(month)}/>
                )}
            </BarChart>
        </Col>
    </>;

    return <Main body={body}/>
}

export default YearAtTheGlanceBarChart;