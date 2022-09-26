import React, {useEffect, useState} from 'react';
import './App.css';

const App = () => {

    const [logs, setLogs] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setLoading(true);

        fetch('/logs/ERROR')
            .then(response => response.json())
            .then(data => {
                setLogs(data);
                setLoading(false);
            })
    }, []);

    if (loading) {
        return <p>Loading...</p>;
    }

    return (
        <div className="App">
            <header className="App-header">
                <div className="App-intro">
                    <h2>Log List</h2>
                    {logs.map(log =>
                        <div key={log.id}>
                            {log.type} - {log.message}
                        </div>
                    )}
                </div>
            </header>
        </div>
    );
}

export default App;