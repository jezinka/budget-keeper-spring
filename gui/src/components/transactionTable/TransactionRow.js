import React from "react";
import {Button} from "react-bootstrap";
import {ArrowsAngleExpand, CheckCircleFill, ExclamationTriangleFill, Pencil, Trash} from "react-bootstrap-icons";
import {formatNumber, UNKNOWN_CATEGORY} from "../../Utils";

const TransactionRow = ({transaction, editTransaction, splitTransaction, deleteTransaction}) => {

    return (
        <tr key={transaction.id}>
            <td>{transaction.transactionDate}</td>
            <td>{transaction.description.substring(0, 80)}</td>
            <td style={{textAlign: 'right', whiteSpace: 'nowrap'}}>
                {formatNumber(transaction.amount)}
                {/* fixed slot keeps amounts aligned whether or not a row has an icon */}
                <span className="d-inline-block ms-1" style={{width: '1.1em'}}>
                    {transaction.transferSettled === true &&
                        <span className="text-success" title="Rozliczone w wyciągu konta"
                              aria-label="Rozliczone w wyciągu konta"><CheckCircleFill/></span>}
                    {transaction.transferSettled === false &&
                        <span className="text-danger" title="Brak w wyciągu - zaimportuj wyciąg"
                              aria-label="Brak w wyciągu - zaimportuj wyciąg"><ExclamationTriangleFill/></span>}
                </span>
            </td>
            <td>{transaction.beneficiaryName}</td>
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