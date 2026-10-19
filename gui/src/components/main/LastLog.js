import React, {useEffect, useState} from 'react';
import Alert from 'react-bootstrap/Alert';
import {DATE_TIME_FORMAT, ERROR_EVENT} from "../../Utils";

const clientError = (message) => ({id: -1, level: 'ERROR', message, date: new Date(), fromClient: true});

export default function LastLog() {
    const [log, setLog] = useState({});

    useEffect(() => {
        async function reloadLogs() {
            const response = await fetch('/budget/logs/forDisplay');
            const data = response.ok ? await response.json() : clientError('Something went wrong!');
            // log z serwera przychodzi asynchronicznie i nie może przykryć błędu zgłoszonego w międzyczasie
            if (data) {
                setLog(current => current.fromClient ? current : data);
            }
        }

        function showError(event) {
            setLog(clientError(event.detail));
        }

        reloadLogs();
        window.addEventListener(ERROR_EVENT, showError);
        return () => window.removeEventListener(ERROR_EVENT, showError);
    }, []);

    if (log !== null && log.message !== undefined) {
        let date = new Date(log.date);
        return (
            <Alert variant={log.level === "INFO" ? "info" : "danger"} key={log.id}>
                {DATE_TIME_FORMAT.format(date)}: {log.level} - {log.message}
            </Alert>
        );
    }
}
