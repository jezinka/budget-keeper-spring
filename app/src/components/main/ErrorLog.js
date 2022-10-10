import React, {useEffect, useState} from 'react';
import Alert from 'react-bootstrap/Alert';

export default function ErrorLog() {
    const [show, setShow] = useState(true);
    const [logs, setLogs] = useState([]);

    async function reloadLogs() {
        const response = await fetch('/logs/ERROR');
        if (response.ok) {
            const data = await response.json();
            if (data) {
                setLogs(data);
            }
        } else {
            setLogs([{id: -1, type: 'ERROR', message: 'Something went wrong!'}]);
        }
    }

    useEffect(() => {
        reloadLogs();
    }, []);

    if (show) {
        return (
            logs.map(log =>
                <Alert variant="danger" onClose={() => setShow(false)} dismissible key={log.id}>
                    {log.type} - {log.message}
                </Alert>
            )
        );
    }
}
