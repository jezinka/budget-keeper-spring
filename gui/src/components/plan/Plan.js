import {Col, Row} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import {formatNumber} from "../../Utils";
import Table from "react-bootstrap/Table";
import Budget from "./Budget";
import BudgetSummary from "./BudgetSummary";

const Plan = () => {
    const [data, setData] = useState([]);

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        const response = await fetch('/budget/fixedCost')
        const data = await response.json();
        setData(data);
    }

    function getPayedSum() {
        return data.filter(d => d.payDate == null).map(x => x.amount).reduce((sum, num) => sum + num, 0);
    }

    let body = <>
        <Row>
            <Col sm={8}>
                <Table responsive='sm' striped bordered size="sm">
                    <thead>
                    <tr className='table-info'>
                        <th>ZAPŁACONE</th>
                        <th>NAZWA</th>
                        <th>ILE</th>
                        <th>KIEDY</th>
                    </tr>
                    </thead>
                    <tbody>
                    {data.map(record => <tr key={record.id}>
                        <td style={{width: "30px", textAlign: "center"}}>
                            <input
                                type="checkbox"
                                id={"payed_" + record.id}
                                name={"payed_" + record.id}
                                checked={record.payDate != null}
                                onChange={async () => {
                                    if (record.payDate != null) return;
                                    const response = await fetch('/budget/fixedCost/' + record.id, {
                                        method: 'PUT'
                                    });
                                    if (response.ok) {
                                        loadData();
                                    }
                                }}
                            />
                        </td>
                        <td>{record.name}</td>
                        <td style={{textAlign: 'right'}}>{formatNumber(record.amount)}</td>
                        <td>{record.payDate}</td>
                    </tr>)}
                    </tbody>
                </Table>
            </Col>
            <Col sm={3}>
                <Table responsive='sm' striped bordered size="sm">
                    <tbody>
                    <tr>
                        <td className='table-info'>ZAPŁACONE</td>
                        <td>{formatNumber(data.filter(d => d.payDate != null).map(x => x.amount).reduce((sum, num) => sum + num, 0))}</td>
                    </tr>
                    <tr>
                        <td className='table-info'>DO ZAPŁATY</td>
                        <td>{formatNumber(getPayedSum())}</td>
                    </tr>
                    <tr>
                        <td className='table-info'>SUMA</td>
                        <td>{formatNumber(data.map(x => x.amount).reduce((sum, num) => sum + num, 0))}</td>
                    </tr>
                    </tbody>
                </Table>
                <h5>PODSUMOWANIE</h5>
                <BudgetSummary/>
            </Col>
        </Row>
        <Row><Budget/></Row>
    </>;
    return <Main body={body}/>;
}

export default Plan;