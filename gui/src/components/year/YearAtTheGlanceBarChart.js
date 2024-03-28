import {Col, Row} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import {Bar, BarChart, Tooltip, XAxis, YAxis} from "recharts";
import {addSumPerMonth, getMonthName, handleError, MONTHS_ARRAY} from "../../Utils";
import YearFilter from "./YearFilter";
import CategoryCheckboxRow from "./CategoryCheckboxRow";

const YearAtTheGlanceBarChart = () => {
    const [data, setData] = useState([])
    const [formState, setFormState] = useState(new Date().getFullYear());
    const [categories, setCategories] = useState([]);
    const [selectedCategories, setSelectedCategories] = useState([]);

    const monthColors = [
        '#A6CEE3', '#1F78B4', '#b413b9', '#33A02C', '#FB9A99', '#E31A1C',
        '#FDBF6F', '#FF7F00', '#CAB2D6', '#6A3D9A', '#16AC1C', '#B15928']

    useEffect(() => {
        loadData();
    }, [formState]);

    async function loadData() {
        const response = await fetch('/budget/expenses/getPivot/' + formState);
        if (response.ok) {
            let data = await response.json();

            addSumPerMonth(data);
            data = data.filter((d) => d.sum < 0);
            const sorted = data.sort((a, b) => a.sum - b.sum);
            const categoriesNames = sorted.map((d) => d.category);

            setCategories(categoriesNames);
            setSelectedCategories(categoriesNames);
            return setData(sorted);
        }
        handleError();
    }

    let body = <>
        <Row>
            <YearFilter formState={formState} formHandler={setFormState}/>
        </Row>
        <CategoryCheckboxRow
            categories={categories}
            selectedCategories={selectedCategories}
            setSelectedCategories={setSelectedCategories}
        />
        <Col>
            <BarChart width={1700}
                      height={800}
                      data={data.filter((d) => selectedCategories.includes(d.category))}>
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
                        fill={monthColors[MONTHS_ARRAY.indexOf(month)]}/>
                )}
            </BarChart>
        </Col>
    </>;

    return <Main body={body}/>
}

export default YearAtTheGlanceBarChart;