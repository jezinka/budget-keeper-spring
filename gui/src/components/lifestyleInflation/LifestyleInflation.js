import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import {Bar, BarChart, CartesianGrid, Legend, Tooltip, XAxis, YAxis} from "recharts";
import {Col, Row} from "react-bootstrap";
import {handleError, monthColors} from "../../Utils";

const LifestyleInflation = () => {
    const [data, setData] = useState([]);
    const [years, setYears] = useState([]);

    useEffect(() => {
        loadData();
    }, []);

    function getSeries() {
        const series = [];
        years.forEach((year, index) => {
            series.push(<Bar dataKey={year} fill={monthColors[index]}/>)
        });
        return series;
    }

    async function loadData() {
        const response = await fetch('/budget/expenses/lifestyleInflation');
        if (response.ok) {
            let data = await response.json();
            let sorted = data.data.sort(function (a, b) {
                return ('' + a.category).localeCompare(b.category);
            });
            setYears([...new Set(sorted.map((k) => Object.keys(k)).flat().filter((k) => k !== 'category'))]);
            return setData(sorted);
        }
        handleError();
    }

    let body = <>
        <Col sm={2}>
            <BarChart
                width={300}
                height={800}
                data={data.filter(c => c.category === "na życie")}>
                <CartesianGrid strokeDasharray="3 3"/>
                <XAxis dataKey="category"/>
                <YAxis/>
                <Tooltip/>
                <Legend/>
                {getSeries()}
            </BarChart>
        </Col><Col>
        <Row>
            {data.filter(c => c.category !== "na życie").map(d => {
                return <BarChart
                    width={300}
                    height={200}
                    data={data.filter(c => c.category === d.category)}>
                    <CartesianGrid strokeDasharray="3 3"/>
                    <XAxis dataKey="category"/>
                    <YAxis/>
                    <Tooltip/>
                    <Legend/>
                    {getSeries()}
                </BarChart>
            })}
        </Row>
    </Col>
    </>

    return <Main body={body}/>
}

export default LifestyleInflation;