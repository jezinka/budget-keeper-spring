import React, {useState} from 'react';

function FileUpload({closeHandler}) {
    const [file, setFile] = useState(null);

    const handleFileChange = (event) => {
        setFile(event.target.files[0]);
    };

    const handleUpload = async () => {
        if (!file) {
            alert('Please select a file first!');
            return;
        }

        const formData = new FormData();
        formData.append('file', file);

        try {
            await fetch('/budget/budgetPlan/upload', {
                method: 'POST',
                body: formData,
            });
            closeHandler();
        } catch (error) {
            closeHandler();
        }
    };

    return (
        <div>
            <input type="file" onChange={handleFileChange}/>
            <button onClick={handleUpload}>Upload</button>
        </div>
    );
}

export default FileUpload;