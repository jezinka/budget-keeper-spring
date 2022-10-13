import React, {useEffect, useState} from 'react';
import Table from "react-bootstrap/Table";

export default function MoneyAmount() {
    const [moneyAmount, setMoneyAmount] = useState({});

    useEffect(() => {
        fetch('/moneyAmount')
            .then(response => response.json())
            .then(data => {
                setMoneyAmount(data);
            })
    }, []);

    return (
        <Table responsive='sm' striped bordered size="sm">
            <tbody>
            <tr>
                <td>NA WEJŚCIU</td>
                <td>{moneyAmount.amount}</td>
            </tr>
            <tr>
                <td>WYDATKI</td>
                <td>{moneyAmount.expenses}</td>
            </tr>
            <tr>
                <td>WPŁYWY</td>
                <td>{moneyAmount.income}</td>
            </tr>
            <tr>
                <td>STAN KONTA</td>
                <td>{moneyAmount.accountBalance}</td>
            </tr>
            </tbody>
        </Table>);
}
