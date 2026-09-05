import Table from "react-bootstrap/Table";
import React from "react";
import {formatNumber} from "../../Utils";

const TransactionCounter = ({transactionCounter, transactionSum}) => {
    return <Table responsive='sm' striped bordered size="sm">
        <tbody>
        <tr>
            <td>Przefiltrowanych:</td>
            <td>{transactionCounter}</td>
            <td>{formatNumber(transactionSum)}</td>
        </tr>
        </tbody>
    </Table>;
}

export default TransactionCounter