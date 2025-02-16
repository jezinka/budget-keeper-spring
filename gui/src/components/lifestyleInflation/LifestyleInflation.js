import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import {Bar, BarChart, CartesianGrid, Legend, Tooltip, XAxis, YAxis} from "recharts";
import {Col} from "react-bootstrap";
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
                {getSeries()}
            </BarChart>
        </Col>
    </>;

    return <Main body={body}/>
}

export default LifestyleInflation;