import {Col} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import {LabelList, Pie, PieChart} from "recharts";
import {addSumPerMonth, getSumFromMap, renderCustomizedLabel} from "../../Utils";

const YearAtTheGlancePieChart = () => {
    const [data, setData] = useState([])

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        const response = await fetch('/groupedExpenses/getPivot')
        let data = await response.json();

        addSumPerMonth(data);
        data = data.filter((d) => d.sum < 0);
        data.forEach((d) => {
            d.sum = Math.abs(d.sum);
        });

        const sum = getSumFromMap(data, 'sum');
        const filteredData = data.filter((d) => {
            return d.sum / sum >= 0.005
        });

        filteredData.push({category: 'inne', sum: sum - getSumFromMap(filteredData, 'sum')});
        setData(filteredData.sort((a, b) => a.sum - b.sum));
    }

    let body = <>
        <Col>
            <PieChart width={1700} height={800}>
                <Pie
                    data={data}
                    cx="50%"
                    cy="50%"
                    outerRadius={300}
                    dataKey="sum"
                    fill="#6BBDFF"
                    label={renderCustomizedLabel}>
                    <LabelList dataKey="category" position="outside" stroke={"black"} offset={20}/>
                </Pie>
            </PieChart>
        </Col>
    </>;

    return <Main body={body}/>
}

export default YearAtTheGlancePieChart;