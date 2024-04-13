import React from 'react';
import {Cell, LabelList, Pie, PieChart} from "recharts";
import {renderCustomizedLabel} from "../../Utils";

const SmallPieChartComponent = ({title, data, colorMap, labelKey}) => {

    const width = 250;
    const height = 150;
    const dataKey = 'amount';

    return (
        <div>
            <>{title}</>
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
        </div>
    );
};

export default SmallPieChartComponent;