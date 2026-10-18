import React, {useEffect, useState} from 'react';
import {Col, Table} from 'react-bootstrap';
import {formatNumber} from '../../Utils';

// Non-zero statement balances of external accounts (e.g. Revolut), one table per account.
const AccountBalances = () => {
    const [balances, setBalances] = useState([]);

    useEffect(() => {
        fetch('/budget/accounts/balances')
            .then(res => res.json())
            .then(setBalances)
            .catch(() => {
            });
    }, []);

    const byAccount = Object.entries(balances
        .filter(b => b.balance != null && b.balance !== 0)
        .reduce((groups, b) => ({...groups, [b.accountName]: [...(groups[b.accountName] || []), b]}), {}));

    return byAccount.map(([accountName, rows]) => (
            <Col sm="auto">
                <h6>{accountName}</h6>
                <Table striped bordered size='sm' style={{width: 'auto'}}>
                    <thead>
                    <tr>
                        <th className="text-end">Saldo</th>
                        <th>Waluta</th>
                    </tr>
                    </thead>
                    <tbody>
                    {rows.map(b => (
                        <tr key={`${b.currency}-${b.product}`}>
                            <td className="text-end">
                                {formatNumber(b.balance)}
                                {!b.matches && <span className="text-danger ms-1"
                                                     title={`Suma transakcji ${formatNumber(b.computed)} różni się od salda - brakuje części historii`}>⚠</span>}
                            </td>
                            <td>{b.currency}{b.product && b.product !== 'Bieżące' ? ` (${b.product})` : ''}</td>
                        </tr>
                    ))}
                    </tbody>
                </Table>
            </Col>
    ));
};

export default AccountBalances;
