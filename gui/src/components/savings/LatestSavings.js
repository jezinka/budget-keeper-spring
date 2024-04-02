import React, {useEffect, useState} from "react";
import {DATE_FORMAT, formatNumber, handleError, monthColors, renderCustomizedLabel} from "../../Utils";
import Table from "react-bootstrap/Table";
import {Col, Container, Row} from "react-bootstrap";
import Main from "../main/Main";
import {CartesianGrid, Cell, LabelList, Legend, Line, LineChart, Pie, PieChart, Tooltip, XAxis, YAxis} from "recharts";

const LatestSavings = () => {
    const [savings, setSavings] = useState([]);
    const [groupedSavings, setGroupedSavings] = useState([]);
    const [savingColorMap, setSavingColorMap] = useState({})

    useEffect(() => {
        loadLatest();
        loadAll();
    }, []);

    async function loadLatest() {
        const response = await fetch('/budget/savingsRead/latest');
        if (response.ok) {
            const data = await response.json();
            if (data) {
                let savingsIdColor = {}
                data.forEach(d => savingsIdColor[d.savingsId] = monthColors[data.indexOf(d)]);
                setSavingColorMap(savingsIdColor);
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
                <Table id='latestSavings' responsive='sm' bordered size="sm">
                    <thead>
                    <tr className='table-info'>
                        <th>GRUPA</th>
                        <th>NAZWA</th>
                        <th>ILE</th>
                        <th>DATA</th>
                    </tr>
                    </thead>
                    <tbody>
                    {savings.map(saving => <tr key={saving.id}
                                               style={{
                                                   backgroundColor: savingColorMap[saving.savingsId] +'40',
                                               }}>
                        <td>{saving.fundGroup}</td>
                        <td>{saving.name}</td>
                        <td>{formatNumber(saving.amount)}</td>
                        <td>{DATE_FORMAT.format(new Date(saving.date))}</td>
                    </tr>)}
                    </tbody>
                </Table>
                <Col sm={4}>
                    <h5>Inwestycje</h5>
                    <PieChart width={600} height={300}>
                        <Pie
                            data={savings.filter(s => s.purpose === 'I').sort((a, b) => a.amount - b.amount)}
                            cx="50%"
                            cy="50%"
                            outerRadius={100}
                            dataKey="amount"
                            label={renderCustomizedLabel}>
                            {
                                savings.filter(s => s.purpose === 'I').sort((a, b) => a.amount - b.amount).map((entry, index) => (
                                    <Cell key={`cell-${index}`} fill={savingColorMap[entry.savingsId]}/>
                                ))
                            }
                            <LabelList dataKey="groupName" position="outside" stroke={"black"} offset={20}/>
                        </Pie>
                    </PieChart>
                </Col>
                <Col sm={4}>
                    <h5>Emerytura</h5>
                    <PieChart width={600} height={300}>
                        <Pie
                            data={savings.filter(s => s.purpose === 'E').sort((a, b) => a.amount - b.amount)}
                            cx="50%"
                            cy="50%"
                            outerRadius={100}
                            dataKey="amount"
                            label={renderCustomizedLabel}>
                            <LabelList dataKey="groupName" position="outside" stroke={"black"} offset={20}/>
                            {
                                savings.filter(s => s.purpose === 'E').sort((a, b) => a.amount - b.amount).map((entry, index) => (
                                    <Cell key={`cell-${index}`} fill={savingColorMap[entry.savingsId]}/>
                                ))
                            }
                        </Pie>
                    </PieChart>
                </Col>
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
                <Tooltip labelFormatter={(label) => {
                    return DATE_FORMAT.format(new Date(label))
                }}/>
                <Legend layout="vertical" align="right" verticalAlign="middle"/>
                {groupedSavings.map((s) => (
                    <Line dataKey="amount"
                          data={s.data}
                          name={s.name}
                          key={s.name}
                          stroke={savingColorMap[s.savingsId]}/>
                ))}
            </LineChart> : <LineChart width={500} height={300}> </LineChart>}
        </Container>
    }/>);
}

export default LatestSavings;