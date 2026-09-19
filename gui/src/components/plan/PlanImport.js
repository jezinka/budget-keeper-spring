import React, {useState} from 'react';

function FileUpload({closeHandler, uploadUrl = '/budget/budgetPlan/upload'}) {
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
            const response = await fetch(uploadUrl, {
                method: 'POST',
                body: formData,
            });
            if (!response.ok) return alert(await response.text());
            closeHandler();
        } catch (error) {
            alert('Błąd sieci: ' + error.message);
        }
    };

    return (
        <div>
            <input type="file" onChange={handleFileChange}/>
            <button onClick={handleUpload}>Importuj</button>
        </div>
    );
}

export default FileUpload;