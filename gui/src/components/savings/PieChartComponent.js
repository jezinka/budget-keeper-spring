import React from 'react';
import {Cell, LabelList, Pie, PieChart} from "recharts";
import {formatNumber, renderCustomizedLabel} from "../../Utils";

const PieChartComponent = ({title, data, colorMap, labelKey}) => {

    const width = 400;
    const height = 300;
    const dataKey = 'amount';

    return (
        <div>
            <h5>{title}</h5>
            <PieChart width={width} height={height}>
                <Pie
                    data={data}
                    cx="50%"
                    cy="50%"
                    outerRadius={height / 3.0}
                    dataKey={dataKey}
                    label={renderCustomizedLabel}>
                    {
                        data.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={colorMap[entry.savingsId]}/>
                        ))
                    }
                    <LabelList dataKey={labelKey} position="outside" stroke={"black"} offset={height / 10}/>
                </Pie>
            </PieChart>
            <h6>Suma: {formatNumber(data.map((d) => d[dataKey]).reduce((a, b) => a + b, 0))}</h6>
        </div>
    );
};

export default PieChartComponent;