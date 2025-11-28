import Table from 'react-bootstrap/Table';
import React, {useState, useMemo} from "react";
import {handleError, formatNumber} from "../../Utils";
import EditTransactionModal from "./EditTransactionModal";
import SplitTransactionModal from "./SplitTransactionModal";
import TransactionRow from "./TransactionRow";

export default function TransactionTable(props) {
    const [showEditForm, setShowEditForm] = useState(false);
    const [showSplitForm, setShowSplitForm] = useState(false);
    const [id, setId] = useState(0);
    const [filters, setFilters] = useState({
        description: '',
        category: ''
    });

    function updateFilter(name, value) {
        setFilters(prev => ({...prev, [name]: value}));
    }

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

    const filteredTransactions = useMemo(() => {
        if (!props.transactions) return [];
        return props.transactions.filter(tx => {
            const txDesc = (tx.description || tx.title || tx.note || '').toString().toLowerCase();
            const txCategory = (tx.category || tx.categoryName || '').toString().toLowerCase();

            if (filters.description) {
                if (!txDesc || !txDesc.includes(filters.description.toLowerCase())) return false;
            }

            if (filters.category) {
                if (!txCategory || !txCategory.includes(filters.category.toLowerCase())) return false;
            }

            return true;
        });
    }, [props.transactions, filters]);

    const sumFilteredExpenses = useMemo(() => {
        if (!filteredTransactions) return 0;
        return filteredTransactions
            .filter(t => t.amount !== undefined && t.amount !== null && Number(t.amount) < 0)
            .reduce((acc, t) => acc + Number(t.amount), 0);
    }, [filteredTransactions]);

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
                <tr>
                    <th/>
                    <th>
                        <input type="text" className="form-control form-control-sm" value={filters.description}
                               onChange={e => updateFilter('description', e.target.value)} placeholder="szukaj opisu"/>
                    </th>
                    <th>
                        {/* read-only cell showing sum of filtered expenses */}
                        <input type="text" readOnly className="form-control form-control-sm text-end"
                               value={formatNumber(sumFilteredExpenses)}/>
                    </th>
                    <th>
                        <input type="text" className="form-control form-control-sm" value={filters.category}
                               onChange={e => updateFilter('category', e.target.value)} placeholder="kategoria"/>
                    </th>
                    <th style={{textAlign: "center"}}>
                        <button className="btn btn-sm btn-secondary me-1"
                                onClick={() => setFilters({description: '', category: ''})}>Wyczyść
                        </button>
                    </th>
                </tr>
                </thead>
                <tbody>
                {filteredTransactions.map(transaction => (
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