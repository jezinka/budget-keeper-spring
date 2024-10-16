import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import {CartesianGrid, Legend, Line, LineChart, Tooltip, XAxis, YAxis} from "recharts";
import {Col, Row} from "react-bootstrap";
import CategoryCheckboxRow from "../year/CategoryCheckboxRow";
import {handleError, monthColors} from "../../Utils";

const LifestyleInflation = () => {
    const [data, setData] = useState([]);
    const [categories, setCategories] = useState([]);
    const [selectedCategories, setSelectedCategories] = useState([]);

    useEffect(() => {
        loadData();
        fetchCategories();
    }, []);

    async function fetchCategories() {
        const response = await fetch('/budget/categories/all');

        if (response.ok) {
            const data = await response.json();
            if (data) {
                setCategories(data);
                setSelectedCategories(data.map(c => c.name));
            }
        } else {
            return handleError();
        }
    }


    async function loadData() {
        const response = await fetch('/budget/expenses/lifestyleInflation');
        if (response.ok) {
            let data = await response.json();
            return setData(data.data);
        }
        handleError();
    }

    let body = <>
        <Row className={"mt-5 mx-2"}>
            <CategoryCheckboxRow
                categories={categories}
                selectedCategories={selectedCategories}
                setSelectedCategories={setSelectedCategories}
            /></Row>
        <Col>
            <LineChart width={1600} height={500} data={data}
                       margin={{top: 30, right: 30, left: 20, bottom: 5}}>
                <CartesianGrid strokeDasharray="3 3"/>
                <XAxis dataKey="date"/>
                <YAxis/>
                <Tooltip/>
                <Legend/>
                {selectedCategories.map((c, index) => <Line key={index} type="monotone" dataKey={c} stroke={monthColors[index%12]}/>)}
            </LineChart>
        </Col>
    </>;

    return <Main body={body}/>
}

export default LifestyleInflation;