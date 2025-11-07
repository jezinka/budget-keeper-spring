import Table from 'react-bootstrap/Table';
import React from "react";
import {formatNumber, UNKNOWN_CATEGORY} from "../../Utils";

const TransactionRowReadOnly = ({transaction}) => {
    return (
        <tr>
            <td>{transaction.description.substring(0, 100)}</td>
            <td style={{textAlign: 'right'}}>{formatNumber(transaction.amount)}</td>
            <td style={{color: (transaction.categoryId === UNKNOWN_CATEGORY ? "lightgray" : "black")}}>{transaction.categoryName}</td>
        </tr>
    );
};

export default function TransactionTableReadOnly(props) {
    // Calculate sum of expenses (negative amounts)
    const expenseSum = props.transactions
        .filter(t => t.amount < 0)
        .reduce((sum, t) => sum + t.amount, 0);
    
    const showSumRow = props.transactions.length > 1;
    
    return (
        <Table responsive='sm' striped bordered size="sm" style={{tableLayout: 'fixed'}}>
            <thead>
            <tr className='table-info'>
                <th style={{width: '60%'}}>OPIS</th>
                <th style={{width: '20%'}}>ILE</th>
                <th style={{width: '20%'}}>KATEGORIA</th>
            </tr>
            </thead>
            <tbody>
            {props.transactions.map(transaction => (
                <TransactionRowReadOnly
                    key={transaction.id}
                    transaction={transaction}
                />
            ))}
            {showSumRow && (
                <tr style={{fontWeight: 'bold'}}>
                    <td>Suma wydatków</td>
                    <td style={{textAlign: 'right'}}>{formatNumber(expenseSum)}</td>
                    <td></td>
                </tr>
            )}
            </tbody>
        </Table>
    );
}
