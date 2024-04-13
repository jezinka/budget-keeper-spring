import React, {useEffect, useState} from "react";
import {DATE_FORMAT, formatNumber, handleError, monthColors} from "../../Utils";
import Table from "react-bootstrap/Table";
import {Col, Container, Row} from "react-bootstrap";
import Main from "../main/Main";
import {CartesianGrid, Legend, Line, LineChart, Tooltip, XAxis, YAxis} from "recharts";
import PieChartComponent from "./PieChartComponent";
import SmallPieChartComponent from "./SmallPieChartComponent";

const LatestSavings = () => {
    const [savings, setSavings] = useState([]);
    const [groupedSavings, setGroupedSavings] = useState([]);
    const [savingColorMap, setSavingColorMap] = useState({})
    const [groupedByPurpose, setGroupedByPurpose] = useState([]);
    const [groupedByRisk, setGroupedByRisk] = useState([]);

    useEffect(() => {
        loadLatest();
        loadAll();
    }, []);

    useEffect(() => {
        groupByPurpose();
        groupByRisk();
    }, [savings]);

    function groupByPurpose() {
        const result = [];
        savings.reduce(function (res, value) {
            let purpose = value.purpose;
            if (!res[purpose]) {
                res[purpose] = {purpose: purpose, amount: 0, savingsId: value.savingsId};
                result.push(res[purpose])
            }
            res[purpose].amount += value.amount;
            return res;
        }, {});
       setGroupedByPurpose(result);
    }

    function groupByRisk() {
        const result = [];
        savings.reduce(function (res, value) {
            let risk = value.risk;
            if (!res[risk]) {
                res[risk] = {risk: risk, amount: 0, savingsId: value.savingsId};
                result.push(res[risk])
            }
            res[risk].amount += value.amount;
            return res;
        }, {});
        setGroupedByRisk(result);
    }

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
        <>
            <Container>
                <Row>
                    <Col sm={3}>
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
                                                           backgroundColor: savingColorMap[saving.savingsId] + '40',
                                                       }}>
                                <td>{saving.fundGroup}</td>
                                <td>{saving.name}</td>
                                <td>{formatNumber(saving.amount)}</td>
                                <td>{DATE_FORMAT.format(new Date(saving.date))}</td>
                            </tr>)}
                            </tbody>
                        </Table>
                    </Col>
                    <Col sm={2}>
                        <Row sm={1}>
                            <SmallPieChartComponent
                                title="Cel"
                                data={groupedByPurpose}
                                colorMap={savingColorMap}
                                labelKey="purpose"
                            /></Row>
                        <Row>
                            <SmallPieChartComponent
                                title="Ryzyko"
                                data={groupedByRisk}
                                colorMap={savingColorMap}
                                labelKey="risk"
                            />
                        </Row>
                    </Col>
                    <Col sm={3}>
                        <PieChartComponent
                            title="Inwestycje"
                            data={savings.filter(s => s.purpose === 'I').sort((a, b) => a.amount - b.amount)}
                            colorMap={savingColorMap}
                            labelKey="groupName"
                        />
                    </Col>
                    <Col sm={3}>
                        <PieChartComponent
                            title="Emerytura"
                            data={savings.filter(s => s.purpose === 'E').sort((a, b) => a.amount - b.amount)}
                            colorMap={savingColorMap}
                            labelKey="groupName"
                        />
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
        </>
    }/>);
}

export default LatestSavings;