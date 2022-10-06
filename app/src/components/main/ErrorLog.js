import React, {useEffect, useState} from 'react';
import Alert from 'react-bootstrap/Alert';

export default function ErrorLog() {
    const [show, setShow] = useState(true);
    const [logs, setLogs] = useState([]);

    useEffect(() => {
        fetch('/logs/ERROR')
            .then(response => response.json())
            .then(data => {
                setLogs(data);
            })
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
