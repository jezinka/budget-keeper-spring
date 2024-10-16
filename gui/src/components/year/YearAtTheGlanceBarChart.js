import {Col, Row} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import {Bar, BarChart, Tooltip, XAxis, YAxis} from "recharts";
import {addSumPerMonth, getMonthName, handleError, monthColors, MONTHS_ARRAY} from "../../Utils";
import YearFilter from "./YearFilter";
import CategoryCheckboxRow from "./CategoryCheckboxRow";

const YearAtTheGlanceBarChart = () => {
    const [data, setData] = useState([])
    const [formState, setFormState] = useState(new Date().getFullYear());
    const [categories, setCategories] = useState([]);
    const [selectedCategories, setSelectedCategories] = useState([]);

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

            setCategories(categoriesNames.map(d => ({ "name": d })));
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