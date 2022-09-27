import Table from 'react-bootstrap/Table';
import {useEffect, useState} from "react";
import {Button} from "react-bootstrap";
import {ArrowsAngleExpand, Pencil, Trash} from "react-bootstrap-icons";

export default function TransactionTable() {
    const [transactions, setTransactions] = useState([]);

    useEffect(() => {
        fetch('/transactions')
            .then(response => response.json())
            .then(data => {
                console.log(data.length);
                setTransactions(data);
            })
    }, []);

    return (
        <Table responsive='sm' striped bordered size="sm">
            <thead>
            <tr className='table-info'>
                <th>KIEDY</th>
                <th>CO</th>
                <th>KTO</th>
                <th>ILE</th>
                <th>KATEGORIA</th>
                <th style={{textAlign: "center"}}>*</th>
            </tr>
            </thead>
            <tbody>
            {transactions.map(transaction =>
                <tr key={transaction.id}>
                    <td>{transaction.transactionDate}</td>
                    <td>{transaction.title}</td>
                    <td>{transaction.payee}</td>
                    <td>{transaction.amount}</td>
                    <td>{transaction.category}</td>
                    <td>
                        <Button variant="outline-primary" size="sm"><Pencil/></Button>{' '}
                        <Button variant="outline-primary" size="sm"><ArrowsAngleExpand/></Button>{' '}
                        <Button variant="outline-primary" size="sm"><Trash/></Button>
                    </td>
                </tr>
            )}
            </tbody>
        </Table>
    );
}