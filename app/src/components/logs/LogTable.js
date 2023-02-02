import {Button} from "react-bootstrap";
import {Trash} from "react-bootstrap-icons";
import Table from "react-bootstrap/Table";
import React, {useEffect, useState} from "react";
import {DATE_TIME_FORMAT, handleError} from "../../Utils";

const LogTable = () => {
    const [logs, setLogs] = useState([]);

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        const response = await fetch('/logs');
        if (response.ok) {
            const data = await response.json();
            if (data) {
                return setLogs(data)
            }
        }
        return handleError();
    }

    async function deleteLog(id) {
        const response = await fetch('/logs/' + id, {method: "DELETE"});
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
        if (log.type === 'ERROR') {
            className.push('error')
        }

        return className.join(' ');
    }

    return (<Table responsive='sm' striped bordered size="sm">
        <thead>
        <tr className='table-info'>
            <th>KIEDY</th>
            <th>CO</th>
            <th>WIADOMOŚĆ</th>
            <th style={{textAlign: "center"}}>*</th>
        </tr>
        </thead>
        <tbody>
        {logs.map(log => <tr key={log.id} className={getClassName(log)}>
            <td>{DATE_TIME_FORMAT.format(new Date(log.date))}</td>
            <td>{log.type}</td>
            <td>{log.message}</td>
            <td style={{textAlign: "center"}}>
                <Button variant="outline-primary" size="sm"
                        onClick={() => deleteLog(log.id)}><Trash/>
                </Button>
            </td>
        </tr>)}
        </tbody>
    </Table>)
}

export default LogTable;