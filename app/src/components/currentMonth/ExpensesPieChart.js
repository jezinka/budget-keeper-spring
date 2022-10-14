import React from "react";
import {LabelList, Pie, PieChart} from "recharts";
import {getSumFromMap, renderCustomizedLabel} from "../../Utils";

const ExpensesPieChart = ({data}) => {

    const prepareData = () => {

        const sum = getSumFromMap(data, 'amount');
        const filteredData = data.filter((d) => {
            return d.amount / sum >= 0.02
        });

        filteredData.push({category: 'inne', amount: sum - getSumFromMap(filteredData, 'amount')});
        return filteredData;
    }

    return (
        <PieChart width={400} height={300}>
            <Pie
                data={prepareData()}
                cx="50%"
                cy="50%"
                outerRadius={120}
                dataKey="amount"
                fill="#6BBDFF"
                label={renderCustomizedLabel}>
                <LabelList dataKey="category" position="outside" stroke={"black"} offset={20}/>
            </Pie>
        </PieChart>
    )
}

export default ExpensesPieChart;