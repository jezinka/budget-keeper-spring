import React, {useEffect, useState} from 'react';
import Alert from 'react-bootstrap/Alert';

export default function ErrorLog() {
    const [log, setLog] = useState({});
    const [variant, setVariant] = useState("danger");

    async function reloadLogs() {
        const response = await fetch('/logs/');
        if (response.ok) {
            const data = await response.json();
            if (data) {
                let displayLog = data.find(({type}) => type === "ERROR");
                if (displayLog === undefined) {
                    displayLog = data.find(({type}) => type === "INFO");
                    setVariant("info")
                }
                setLog(displayLog);
            }
        } else {
            setLog({id: -1, type: 'ERROR', message: 'Something went wrong!'});
        }
    }

    useEffect(() => {
        reloadLogs();
    }, []);

    if (log?.id) {
        let date = new Date(log.date);
        let options = {dateStyle: 'medium', timeStyle: 'medium'};
        const dateFormat = new Intl.DateTimeFormat('default', options);


        return (
            <Alert variant={variant} key={log.id}>
                {dateFormat.format(date)}: {log.type} - {log.message}
            </Alert>
        );
    }
}
