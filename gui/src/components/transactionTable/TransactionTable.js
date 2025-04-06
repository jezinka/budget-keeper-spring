import Table from 'react-bootstrap/Table';
import React, {useEffect, useState} from "react";
import {Button} from "react-bootstrap";
import {ArrowsAngleExpand, Pencil, Trash} from "react-bootstrap-icons";
import {formatNumber, handleError, UNKNOWN_CATEGORY} from "../../Utils";
import EditTransactionModal from "./EditTransactionModal"
import SplitTransactionModal from "./SplitTransactionModal";

export default function TransactionTable(props) {
    const [showForm, setShowForm] = useState(false);
    const [id, setId] = useState(0);
    const [splitFlow, setSplitFlow] = useState(false);

    async function reloadTable() {
        setShowForm(false);
        setSplitFlow(false);
        props.changeTransactionsHandler();
    }

    async function deleteTransaction(id) {
        const response = await fetch('/budget/expenses/' + id, {method: 'DELETE'})
        if (response.ok) {
            return reloadTable();
        }
        return handleError();
    }

    useEffect(() => {
        if (id !== 0) {
            setShowForm(true);
        }
    }, [id]);

    async function splitTransaction(id) {
        setSplitFlow(true);
        await setId(id);
    }

    const handleClose = () => {
        setId(0);
        setShowForm(false);
        setSplitFlow(false);
    }

    return (<>
            <EditTransactionModal show={showForm} closeHandler={handleClose} splitFlow={splitFlow} id={id}
                                  changeTransactionsHandler={props.changeTransactionsHandler}/>

            <SplitTransactionModal show={showForm} closeHandler={handleClose} splitFlow={splitFlow} id={id}
                                   changeTransactionsHandler={props.changeTransactionsHandler}/>

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
                {props.transactions.map(transaction => {
                    return <tr key={transaction.id}>
                        <td>{transaction.transactionDate}</td>
                        <td>{transaction.title.substring(0, 50)}</td>
                        <td>{transaction.payee.substring(0, 50)}</td>
                        <td style={{textAlign: 'right'}}>{formatNumber(transaction.amount)}</td>
                        <td style={{color: (transaction.categoryId === UNKNOWN_CATEGORY ? "lightgray" : "black")}}>{transaction.categoryName}</td>
                        <td style={{textAlign: "center"}}>
                            <Button variant="outline-primary" size="sm"
                                    onClick={() => setId(transaction.id)}><Pencil/>
                            </Button>{' '}
                            <Button variant="outline-primary" size="sm"
                                    onClick={() => splitTransaction(transaction.id)}><ArrowsAngleExpand/>
                            </Button>{' '}
                            <Button variant="outline-primary" size="sm"
                                    onClick={() => deleteTransaction(transaction.id)}><Trash/>
                            </Button>
                        </td>
                    </tr>;
                })}
                </tbody>
            </Table>
        </>
    );
}