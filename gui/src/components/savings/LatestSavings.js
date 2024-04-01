import React, {useEffect, useState} from "react";
import {DATE_FORMAT, formatNumber, handleError, renderCustomizedLabel} from "../../Utils";
import Table from "react-bootstrap/Table";
import {Container, Row} from "react-bootstrap";
import Main from "../main/Main";
import {CartesianGrid, LabelList, Legend, Line, LineChart, Pie, PieChart, Tooltip, XAxis, YAxis} from "recharts";

const LatestSavings = () => {
    const [savings, setSavings] = useState([]);
    const [groupedSavings, setGroupedSavings] = useState([]);

    useEffect(() => {
        loadLatest();
        loadAll();
    }, []);

    async function loadLatest() {
        const response = await fetch('/budget/savingsRead/latest');
        if (response.ok) {
            const data = await response.json();
            if (data) {
                return setSavings(data)
            }
        }
        return handleError();
    }

    async function loadAll() {
        const response = await fetch('/budget/savingsRead/readAllGrouped');
        if (response.ok) {
            const data = await response.json();
            if (data) {
                return setGroupedSavings(data)
            }
        }
        return handleError();
    }

    return (<Main body={
        <Container>
            <Row sm={5}>
                <Table responsive='sm' striped bordered size="sm">
                    <thead>
                    <tr className='table-info'>
                        {/*<th><input type="checkbox"/></th>*/}
                        <th>GRUPA</th>
                        <th>NAZWA</th>
                        <th>ILE</th>
                        <th>DATA</th>
                    </tr>
                    </thead>
                    <tbody>
                    {savings.map(saving => <tr key={saving.id}>
                        {/*<td><input type="checkbox"/></td>*/}
                        <td>{saving.fundGroup}</td>
                        <td>{saving.name}</td>
                        <td>{formatNumber(saving.amount)}</td>
                        <td>{DATE_FORMAT.format(new Date(saving.date))}</td>
                    </tr>)}
                    </tbody>
                </Table>
                <PieChart width={500} height={400}>
                    <Pie
                        data={savings.sort((a, b) => a.amount - b.amount)}
                        cx="50%"
                        cy="50%"
                        outerRadius={100}
                        dataKey="amount"
                        fill="#6BBDFF"
                        label={renderCustomizedLabel}>
                        <LabelList dataKey="groupName" position="outside" stroke={"black"} offset={20}/>
                    </Pie>
                </PieChart>
            </Row>
            {groupedSavings != null ? <LineChart width={1500} height={400}>
                <CartesianGrid strokeDasharray="3 3"/>
                <XAxis dataKey="date" type="number"
                       allowDuplicatedCategory={false}
                       domain={['dataMin', 'dataMax']}
                       tickFormatter={(tickItem) => {
                           return DATE_FORMAT.format(new Date(tickItem))
                       }}/>
                <YAxis dataKey="amount" scale='log' domain={['auto', 'auto']}/>
                <Tooltip/>
                <Legend/>
                {groupedSavings.map((s) => (
                    <Line dataKey="amount"
                          data={s.data}
                          name={s.name}
                          key={s.name}/>
                ))}
            </LineChart> : <LineChart width={500} height={300}> </LineChart>}
        </Container>
    }/>);
}

export default LatestSavings;