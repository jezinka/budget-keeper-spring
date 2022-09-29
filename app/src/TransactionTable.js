import Table from 'react-bootstrap/Table';
import {useEffect, useState} from "react";
import {Button} from "react-bootstrap";
import {ArrowsAngleExpand, Pencil, Trash} from "react-bootstrap-icons";

export default function TransactionTable() {
    const [transactions, setTransactions] = useState([]);

    async function reloadTable() {
        const response = await fetch('/transactions');
        const data = await response.json();
        console.log(data.length);
        setTransactions(data);
    }

    useEffect(() => {
        reloadTable();
    }, []);

    async function deleteTransaction(id) {
        const success = await fetch("/transactions/" + id, {method: 'DELETE'})
        const data = await success.json();
        if (data) {
            reloadTable();
        }
    }

    const handleDeleteClick = (e) => deleteTransaction(e.currentTarget.value);

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
                        <Button variant="outline-primary" size="sm"
                                value={transaction.id}
                                onClick={handleDeleteClick}><Trash/>
                        </Button>
                    </td>
                </tr>
            )}
            </tbody>
        </Table>
    );
}