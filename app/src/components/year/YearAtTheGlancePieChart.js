import {Button, Col, Form, Spinner} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import {LabelList, Pie, PieChart} from "recharts";
import {addSumPerMonth, getSumFromMap, handleError, renderCustomizedLabel} from "../../Utils";
import {ArrowClockwise} from "react-bootstrap-icons";

const YearAtTheGlancePieChart = () => {
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
            data.forEach((d) => {
                d.sum = Math.abs(d.sum);
            });

            const sum = getSumFromMap(data, 'sum');
            const filteredData = data.filter((d) => {
                return d.sum / sum >= 0.005
            });

            filteredData.push({category: 'inne', sum: sum - getSumFromMap(filteredData, 'sum')});
            return setData(filteredData.sort((a, b) => a.sum - b.sum));
        }
        handleError();
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
            <Button className={"mt-2"} size={"sm"} onClick={loadData}>
                {showSpinner ? <Spinner size={"sm"} animation="grow"/> : <ArrowClockwise/>}
            </Button>
        </Col>
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