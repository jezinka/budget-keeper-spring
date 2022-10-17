import {Button, Col, Form, Spinner} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import {Bar, BarChart, Tooltip, XAxis, YAxis} from "recharts";
import {addSumPerMonth, getMonthName, handleError, MONTHS_ARRAY} from "../../Utils";
import {ArrowClockwise} from "react-bootstrap-icons";

const YearAtTheGlanceBarChart = () => {
    const [data, setData] = useState([])
    const [showSpinner, setShowSpinner] = useState(false);
    const [formState, setFormState] = useState({year: new Date().getFullYear()});

    const handleChange = (event) => {
        setFormState({...formState, [event.target.name]: event.target.value});
    };

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        setShowSpinner(true);
        const response = await fetch('/groupedExpenses/getPivot', {
            method: "POST",
            body: JSON.stringify(formState),
            headers: {'Content-Type': 'application/json'}
        })
        setShowSpinner(false);
        if (response.ok) {
            let data = await response.json();

            addSumPerMonth(data);
            data = data.filter((d) => d.sum < 0);
            return setData(data.sort((a, b) => a.sum - b.sum));
        }
        handleError();
    }

    function getColor(month) {
        return MONTHS_ARRAY.indexOf(month) % 2 === 0 ? "#8884d8" : "#82ca9d";
    }

    let body = <>
        <Col sm={1}>
            <Form>
                <Form.Select className="m-2" size="sm" placeholder="Rok:" onChange={handleChange}
                             name="year" value={formState.year}>
                    {[2022, 2021].map((year) => <option key={year} value={year}>{year}</option>)}
                </Form.Select>
            </Form>
        </Col>
        <Col sm={1}>
            <Button className={"mt-2"} size={"sm"} onClick={loadData}>{showSpinner ?
                <Spinner size={"sm"} animation="grow"/> : <ArrowClockwise/>}</Button>
        </Col>
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