import React from "react";
import {Button} from "react-bootstrap";
import {ArrowsAngleExpand, Pencil, Trash} from "react-bootstrap-icons";
import {formatNumber, UNKNOWN_CATEGORY} from "../../Utils";

const TransactionRow = ({transaction, editTransaction, splitTransaction, deleteTransaction}) => {

    return (
        <tr key={transaction.id}>
            <td>{transaction.transactionDate}</td>
            <td>{transaction.title.substring(0, 50)}</td>
            <td>{transaction.payee.substring(0, 50)}</td>
            <td style={{textAlign: 'right'}}>{formatNumber(transaction.amount)}</td>
            <td style={{color: (transaction.categoryId === UNKNOWN_CATEGORY ? "lightgray" : "black")}}>{transaction.categoryName}</td>
            <td style={{textAlign: "center"}}>
                <Button variant="outline-primary" size="sm"
                        onClick={() => editTransaction(transaction.id)}><Pencil/></Button>{' '}
                <Button variant="outline-primary" size="sm"
                        onClick={() => splitTransaction(transaction.id)}><ArrowsAngleExpand/></Button>{' '}
                <Button variant="outline-primary" size="sm"
                        onClick={() => deleteTransaction(transaction.id)}><Trash/></Button>
            </td>
        </tr>
    );
};

export default TransactionRow;