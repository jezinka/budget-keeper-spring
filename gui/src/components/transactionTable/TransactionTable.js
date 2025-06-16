import Table from 'react-bootstrap/Table';
import React, {useState} from "react";
import {handleError} from "../../Utils";
import EditTransactionModal from "./EditTransactionModal";
import SplitTransactionModal from "./SplitTransactionModal";
import TransactionRow from "./TransactionRow";

export default function TransactionTable(props) {
    const [showEditForm, setShowEditForm] = useState(false);
    const [showSplitForm, setShowSplitForm] = useState(false);
    const [id, setId] = useState(0);

    async function reloadTable() {
        setShowEditForm(false);
        setShowSplitForm(false);
        props.changeTransactionsHandler();
    }

    async function deleteTransaction(id) {
        const response = await fetch('/budget/expenses/' + id, {method: 'DELETE'});
        if (response.ok) {
            return reloadTable();
        }
        return handleError();
    }

    async function editTransaction(selectedId) {
        setId(selectedId);
        setShowEditForm(true);
    }

    async function splitTransaction(selectedId) {
        setId(selectedId);
        setShowSplitForm(true)
    }

    return (
        <>
            <EditTransactionModal show={showEditForm} id={id}
                                  changeTransactionsHandler={props.changeTransactionsHandler}
                                  closeHandler={() => {
                                      setId(0);
                                      setShowEditForm(false);
                                  }}/>

            <SplitTransactionModal show={showSplitForm} id={id}
                                   changeTransactionsHandler={props.changeTransactionsHandler}
                                   closeHandler={() => {
                                       setId(0);
                                       setShowSplitForm(false);
                                   }}/>

            <Table responsive='sm' striped bordered size="sm">
                <thead>
                <tr className='table-info'>
                    <th>KIEDY</th>
                    <th>OPIS</th>
                    <th>ILE</th>
                    <th>KATEGORIA</th>
                    <th style={{textAlign: "center"}}>*</th>
                </tr>
                </thead>
                <tbody>
                {props.transactions.map(transaction => (
                    <TransactionRow
                        key={transaction.id}
                        transaction={transaction}
                        editTransaction={editTransaction}
                        splitTransaction={splitTransaction}
                        deleteTransaction={deleteTransaction}
                    />
                ))}
                </tbody>
            </Table>
        </>
    );
}