import React, {useEffect, useState} from 'react';
import Main from '../main/Main';
import Table from 'react-bootstrap/Table';
import {formatNumber} from '../../Utils';

const SinkingFundsView = () => {
    const [accounts, setAccounts] = useState([]);

    useEffect(() => {
        fetch('/budget/accounts/sinking-funds')
            .then(res => res.json())
            .then(setAccounts)
            .catch(() => {});
    }, []);

    return (
        <Main body={
            <Table responsive='sm' striped bordered size='sm' className='mt-3'>
                <thead>
                    <tr>
                        <th>Konto</th>
                        <th>Stan</th>
                        <th>Uwagi</th>
                    </tr>
                </thead>
                <tbody>
                    {accounts.map(a => (
                        <tr key={a.id}>
                            <td>{a.name}</td>
                            <td className={a.balance < 0 ? 'text-danger' : ''}>{formatNumber(a.balance)}</td>
                            <td>{a.note}</td>
                        </tr>
                    ))}
                </tbody>
            </Table>
        }/>
    );
};

export default SinkingFundsView;
