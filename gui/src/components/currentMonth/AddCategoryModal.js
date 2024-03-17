import React, {useState} from "react";
import {Button, Modal} from "react-bootstrap";

const AddCategoryForm = (props) => {
    const [name, setName] = useState("");

    const handleSubmit = async () => {
        await fetch('/budget/categories', {
            method: 'POST', body: JSON.stringify({"name": name}), headers: {'Content-Type': 'application/json'},
        });
        props.close();
        setName("");
    };

    const handleChange = (e) => {
        setName(e.target.value);
    };

    return (
        <Modal show={props.show} onHide={props.close}>
            <Modal.Header closeButton>
                <Modal.Title>Nowa kategoria</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <input type="text" value={name} onChange={handleChange} placeholder="Kategoria"/>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={props.close}>
                    Zamknij
                </Button>
                <Button variant="primary" onClick={handleSubmit}>
                    Zapisz
                </Button>
            </Modal.Footer>
        </Modal>
    );
}

export default AddCategoryForm;
