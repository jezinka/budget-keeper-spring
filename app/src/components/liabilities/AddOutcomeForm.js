import {Button, Form, Modal} from "react-bootstrap";
import {handleError} from "../../Utils";

const AddOutcomeForm = ({formState, setFormState, setShowForm, showForm, reloadHandler}) => {

    async function submitForm() {
        const response = await fetch('/liabilityLookouts/', {
            method: 'POST',
            body: JSON.stringify(formState),
            headers: {'Content-Type': 'application/json'},
        });
        if (response.ok) {
            setShowForm(false);
            return await reloadHandler();
        }
        handleError();
    }

    const handleClose = () => setShowForm(false);

    const handleChange = (event) => {
        setFormState({...formState, [event.target.name]: event.target.value});
    };

    return <Modal show={showForm} onHide={handleClose}>
        <Modal.Header closeButton>
            <Modal.Title>Dodaj wynik</Modal.Title>
        </Modal.Header>
        <Modal.Body>
            <Form>
                <Form.Group className="mb-3">
                    <Form.Label>Kiedy:</Form.Label>
                    <Form.Control type="date" onChange={handleChange} name="date"
                                  value={formState.date}/>
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Ile:</Form.Label>
                    <Form.Control type="number" onChange={handleChange} name="outcome"
                                  value={formState.outcome}
                                  autoFocus/>
                </Form.Group>
            </Form>
        </Modal.Body>
        <Modal.Footer>
            <Button variant="secondary" onClick={handleClose}>
                Zamknij
            </Button>
            <Button variant="primary" onClick={submitForm}>
                Zapisz
            </Button>
        </Modal.Footer>
    </Modal>
}

export default AddOutcomeForm;