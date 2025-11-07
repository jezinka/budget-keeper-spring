import Table from 'react-bootstrap/Table';
import React from "react";
import {formatNumber, UNKNOWN_CATEGORY} from "../../Utils";

const TransactionRowReadOnly = ({transaction}) => {
    return (
        <tr key={transaction.id}>
            <td>{transaction.transactionDate}</td>
            <td>{transaction.description.substring(0, 100)}</td>
            <td style={{textAlign: 'right'}}>{formatNumber(transaction.amount)}</td>
            <td style={{color: (transaction.categoryId === UNKNOWN_CATEGORY ? "lightgray" : "black")}}>{transaction.categoryName}</td>
        </tr>
    );
};

export default function TransactionTableReadOnly(props) {
    return (
        <Table responsive='sm' striped bordered size="sm">
            <thead>
            <tr className='table-info'>
                <th>KIEDY</th>
                <th>OPIS</th>
                <th>ILE</th>
                <th>KATEGORIA</th>
            </tr>
            </thead>
            <tbody>
            {props.transactions.map(transaction => (
                <TransactionRowReadOnly
                    key={transaction.id}
                    transaction={transaction}
                />
            ))}
            </tbody>
        </Table>
    );
}
