import {Col, Row} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import {LabelList, Pie, PieChart} from "recharts";
import {addSumPerMonth, getSumFromMap, handleError, renderCustomizedLabel} from "../../Utils";
import YearFilter from "./YearFilter";
import CategoryCheckboxRow from "./CategoryCheckboxRow";

const YearAtTheGlancePieChart = () => {
    const [data, setData] = useState([]);
    const [categories, setCategories] = useState([]);
    const [selectedCategories, setSelectedCategories] = useState([]);
    const [formState, setFormState] = useState(new Date().getFullYear());

    useEffect(() => {
        loadData();
    }, [formState]);

    async function loadData() {
        const response = await fetch('/budget/expenses/getPivot/' + formState);
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
            const sorted = filteredData.sort((a, b) => a.sum - b.sum);
            const categoriesNames = sorted.map((d) => d.category);

            setCategories(categoriesNames.reverse());
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
            <PieChart width={1700} height={800}>
                <Pie
                    data={data.filter((d) => selectedCategories.includes(d.category))}
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