import Table from 'react-bootstrap/Table';
import React from "react";
import {formatNumber, UNKNOWN_CATEGORY} from "../../Utils";

const TransactionRowReadOnly = ({transaction, showDate, isFirstOfDay, dayIndex}) => {
    // Alternate background color per day
    const backgroundColor = dayIndex % 2 === 0 ? '#ffffff' : '#f8f9fa';
    
    return (
        <tr style={{
            backgroundColor: backgroundColor,
            borderTop: isFirstOfDay ? '3px solid #495057' : undefined
        }}>
            {showDate && <td>{transaction.transactionDate}</td>}
            <td>{transaction.description.substring(0, 100)}</td>
            <td style={{textAlign: 'right'}}>{formatNumber(transaction.amount)}</td>
            <td style={{color: (transaction.categoryId === UNKNOWN_CATEGORY ? "lightgray" : "black")}}>{transaction.categoryName}</td>
        </tr>
    );
};

export default function TransactionTableReadOnly(props) {
    const showDate = props.showDate || false;
    
    // Track day changes for visual separation
    let previousDate = null;
    let dayIndex = -1;
    
    return (
        <Table responsive='sm' bordered size="sm" style={{width: 'auto', maxWidth: '100%'}}>
            <thead>
            <tr className='table-info'>
                {showDate && <th style={{width: '120px'}}>KIEDY</th>}
                <th>OPIS</th>
                <th style={{width: '120px', textAlign: 'right'}}>ILE</th>
                <th style={{width: '150px'}}>KATEGORIA</th>
            </tr>
            </thead>
            <tbody>
            {props.transactions.map(transaction => {
                const currentDate = transaction.transactionDate;
                const isFirstOfDay = currentDate !== previousDate;
                
                if (isFirstOfDay) {
                    dayIndex++;
                }
                
                previousDate = currentDate;
                
                return (
                    <TransactionRowReadOnly
                        key={transaction.id}
                        transaction={transaction}
                        showDate={showDate}
                        isFirstOfDay={isFirstOfDay}
                        dayIndex={dayIndex}
                    />
                );
            })}
            </tbody>
        </Table>
    );
}
