import React, {useEffect, useState} from 'react';
import Main from '../main/Main';
import Table from 'react-bootstrap/Table';
import {formatNumber} from '../../Utils';
import {Modal} from "react-bootstrap";

const SinkingFundsView = () => {
    const [accounts, setAccounts] = useState([]);
    const [show, setShow] = useState(false);
    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);
    const [transactionsDetails, setTransactionsDetails] = useState([]);

    useEffect(() => {
        fetch('/budget/accounts/sinking-funds')
            .then(res => res.json())
            .then(setAccounts)
            .catch(() => {
            });
    }, []);

    const renderTooltip = async (a) => {

        const response = await fetch('/budget/expenses/sinkingFunds/' + a.name, {
            method: "GET",
            headers: {'Content-Type': 'application/json'},
        });

        const data = await response.json()
        handleShow();
        setTransactionsDetails(<Table>
            <tbody>
            {data.map((row) => {
                return <tr key={row.title + row.transactionDate}>
                    <td style={{minWidth: '80px'}}>{row.transactionDate}</td>
                    <td>{row.description.substring(0,80)}</td>
                    <td style={{textAlign: 'right'}}>{formatNumber(row.amount)}</td>
                </tr>
            })}

            </tbody>
        </Table>)
    };

    return (
        <>
            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton> <Modal.Title>Transakcje</Modal.Title> </Modal.Header>
                <Modal.Body>{transactionsDetails}</Modal.Body>
            </Modal>
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
                            <td className={a.balance < 0 ? 'text-danger' : ''} onClick={() => renderTooltip(a)}>{formatNumber(a.balance)}</td>
                            <td>{a.note}</td>
                        </tr>
                    ))}
                    </tbody>
                </Table>
            }/>
        </>
    );
};

export default SinkingFundsView;
