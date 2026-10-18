import {Button} from "react-bootstrap";
import {Trash} from "react-bootstrap-icons";
import Table from "react-bootstrap/Table";
import React, {useEffect, useState} from "react";
import {DATE_TIME_FORMAT, handleError} from "../../Utils";

const LogTable = () => {
    const [logs, setLogs] = useState([]);
    const [expenseId, setExpenseId] = useState(null);

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        const response = await fetch('/budget/logs');
        if (response.ok) {
            const data = await response.json();
            if (data) {
                return setLogs(data)
            }
        }
        return handleError();
    }

    async function deleteLog(id) {
        const response = await fetch('/budget/logs/' + id, {method: "DELETE"});
        if (response.ok) {
            return loadData();
        }
        return handleError();
    }

    function getClassName(log) {
        let className = [];
        if (log.deleted) {
            className.push('inactive')
        }
        if (log.level === 'ERROR') {
            className.push('error')
        }

        return className.join(' ');
    }

    const shown = expenseId === null ? logs : [...logs].filter(log => log.expenseId === expenseId).reverse();

    return (<>
        {expenseId !== null && <Button variant="outline-secondary" size="sm" className="mb-2"
                                       onClick={() => setExpenseId(null)}>Historia wydatku #{expenseId} ✕</Button>}
        <Table responsive='sm' striped bordered size="sm">
        <thead>
        <tr className='table-info'>
            <th>KIEDY</th>
            <th>CO</th>
            <th>WYDATEK</th>
            <th>WIADOMOŚĆ</th>
            <th style={{textAlign: "center"}}>*</th>
        </tr>
        </thead>
        <tbody>
        {shown.map(log => <tr key={log.id} className={getClassName(log)}>
            <td>{DATE_TIME_FORMAT.format(new Date(log.date))}</td>
            <td>{log.level}</td>
            <td>{log.expenseId && <Button variant="link" size="sm" className="p-0"
                                          onClick={() => setExpenseId(log.expenseId)}>#{log.expenseId}</Button>}</td>
            <td>{log.message}</td>
            <td style={{textAlign: "center"}}>
                <Button variant="outline-primary" size="sm"
                        onClick={() => deleteLog(log.id)}><Trash/>
                </Button>
            </td>
        </tr>)}
        </tbody>
    </Table>
    </>)
}

export default LogTable;