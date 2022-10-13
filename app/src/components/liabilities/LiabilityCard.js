import Card from 'react-bootstrap/Card';
import {Button, Form, Modal} from "react-bootstrap";
import {GraphUp, PlusLg} from "react-bootstrap-icons";
import {useState} from "react";
import {EMPTY_OPTION, getDate, handleError} from "../../Utils";

function LiabilityCard({liability, reloadHandler}) {
    const [showForm, setShowForm] = useState(false);
    const [formState, setFormState] = useState({
        "date": getDate(), "liability": EMPTY_OPTION, "outcome": 0
    })

    const handleChange = (event) => {
        setFormState({...formState, [event.target.name]: event.target.value});
    };

    async function addOutcome(liability) {
        setFormState({
            "liability": liability, "date": getDate(), outcome: 0,
        })
        setShowForm(true);
    }

    const handleClose = () => setShowForm(false);

    async function submitForm() {
        const response = await fetch('/liabilities/' + formState.liability, {
            method: 'PUT',
            body: JSON.stringify(formState),
            headers: {'Content-Type': 'application/json'},
        });
        if (response.ok) {
            const data = await response.json();
            if (data) {
                setShowForm(false);
                return await reloadHandler();
            }
        }
        handleError();
    }

    return (<>
        <Modal show={showForm} onHide={handleClose}>
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
                                      value={formState.outcome}/>
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

        <Card style={{width: '18rem'}} className="m-2">
            <Card.Header>{liability.bank}</Card.Header>
            <Card.Body>
                <Card.Title>{liability.name}</Card.Title>
                <Card.Subtitle className="mb-2 text-muted">{liability.outcome}</Card.Subtitle>
                <Button size='sm' variant="primary" className="m-1"
                        onClick={() => addOutcome(liability.id)}><PlusLg/></Button>
                <Button size='sm' variant="primary" className="m-1"><GraphUp/></Button>
            </Card.Body>
            <Card.Footer>{liability.date}</Card.Footer>
        </Card>
    </>);
}

export default LiabilityCard;