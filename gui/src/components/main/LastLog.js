import React, {useEffect, useState} from 'react';
import Alert from 'react-bootstrap/Alert';
import {DATE_TIME_FORMAT, ERROR_EVENT} from "../../Utils";

export default function LastLog() {
    const [log, setLog] = useState({});
    const [variant, setVariant] = useState("danger");

    async function reloadLogs() {
        const response = await fetch('/budget/logs/forDisplay');
        if (response.ok) {
            const data = await response.json();
            if (data) {
                if (data.level === "INFO") {
                    setVariant("info")
                }
                setLog(data);
            }
        } else {
            setLog({id: -1, level: 'ERROR', message: 'Something went wrong!', date: new Date()});
        }
    }

    useEffect(() => {
        reloadLogs();

        function showError(event) {
            setVariant("danger");
            setLog({id: -1, level: 'ERROR', message: event.detail, date: new Date()});
        }

        window.addEventListener(ERROR_EVENT, showError);
        return () => window.removeEventListener(ERROR_EVENT, showError);
    }, []);

    if (log !== null && log.message !== undefined) {
        let date = new Date(log.date);
        return (
            <Alert variant={variant} key={log.id}>
                {DATE_TIME_FORMAT.format(date)}: {log.level} - {log.message}
            </Alert>
        );
    }
}
