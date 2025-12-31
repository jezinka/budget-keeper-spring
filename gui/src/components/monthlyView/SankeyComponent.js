import ReactECharts from 'echarts-for-react';
import {useEffect, useState} from "react";

const SankeyComponent = ({year, month}) => {
    const [data, setData] = useState([]);
    const [links, setLinks] = useState([]);

    useEffect(() => {

        async function loadData() {
            let response;
            if (month == null) {
                response = await fetch(`/budget/expenses/yearlyBudgetFlow?year=${year}`);
            } else {
                response = await fetch(`/budget/expenses/monthlyBudgetFlow?year=${year}&month=${month}`);
            }
            const data = await response.json();
            setData(data.nodes);
            setLinks(data.links);
        }

        loadData();
    }, [year, month]);

    const option = {
        series: {
            type: 'sankey',
            layout: 'none',
            emphasis: {
                focus: 'adjacency'
            },
            data: data,
            links: links
        }
    };
    return <ReactECharts option={option} style={{height: '400px'}}/>
};

export default SankeyComponent;