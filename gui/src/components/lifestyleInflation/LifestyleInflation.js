import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import {Bar, BarChart, CartesianGrid, Legend, Rectangle, Tooltip, XAxis, YAxis} from "recharts";
import {Col} from "react-bootstrap";
import {handleError} from "../../Utils";

const LifestyleInflation = () => {
    const [data, setData] = useState([]);

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        const response = await fetch('/budget/expenses/lifestyleInflation');
        if (response.ok) {
            let data = await response.json();
            let sorted = data.data.sort(function (a, b) {
                return ('' + a.category).localeCompare(b.category);
            })
            return setData(sorted);
        }
        handleError();
    }

    let body = <>

        <Col>
            <BarChart
                width={1600}
                height={500}
                data={data}
                margin={{top: 50, right: 30, left: 20, bottom: 5,}}>
                <CartesianGrid strokeDasharray="3 3"/>
                <XAxis dataKey="category"/>
                <YAxis/>
                <Tooltip/>
                <Legend/>
                <Bar dataKey="2025" fill="#8884d8" activeBar={<Rectangle fill="pink" stroke="blue"/>}/>
                <Bar dataKey="2024" fill="#82ca9d" activeBar={<Rectangle fill="gold" stroke="purple"/>}/>
                <Bar dataKey="2023" fill="#8884d8" activeBar={<Rectangle fill="pink" stroke="blue"/>}/>
                <Bar dataKey="2022" fill="#82ca9d" activeBar={<Rectangle fill="gold" stroke="purple"/>}/>
                <Bar dataKey="2021" fill="#8884d8" activeBar={<Rectangle fill="pink" stroke="blue"/>}/>
            </BarChart>
        </Col>
    </>;

    return <Main body={body}/>
}

export default LifestyleInflation;