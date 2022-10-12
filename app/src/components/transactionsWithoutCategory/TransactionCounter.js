import Table from "react-bootstrap/Table";
import React from "react";

const TransactionCounter = ({transactionCounter}) => {
    return <Table responsive='sm' striped bordered size="sm">
        <tbody>
        <tr>
            <td>Bez kategorii:</td>
            <td>{transactionCounter}</td>
        </tr>
        </tbody>
    </Table>;
}

export default TransactionCounter