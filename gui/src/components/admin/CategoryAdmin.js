import React, {useEffect, useState} from "react";
import {Button, Col, Form, Modal, Row, Table} from "react-bootstrap";
import {PencilSquare, PlusCircle, Trash} from "react-bootstrap-icons";
import Main from "../main/Main";

const EMPTY_FORM = {id: null, name: "", useInYearlyCharts: true, level: ""};
const EMPTY_LEVEL_FORM = {level: "", name: ""};

export const CategoryAdminContent = () => {
    const [categories, setCategories] = useState([]);
    const [levels, setLevels] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [formState, setFormState] = useState(EMPTY_FORM);
    const [editId, setEditId] = useState(null);
    const [showLevelModal, setShowLevelModal] = useState(false);
    const [levelFormState, setLevelFormState] = useState(EMPTY_LEVEL_FORM);

    useEffect(() => {
        loadCategories();
        loadLevels();
    }, []);

    async function loadCategories() {
        const res = await fetch("/budget/categories/all");
        setCategories(await res.json());
    }

    async function loadLevels() {
        const res = await fetch("/budget/categories/levels");
        setLevels(await res.json());
    }

    const openAdd = () => {
        setEditId(null);
        setFormState(EMPTY_FORM);
        setShowModal(true);
    };

    const openEdit = (cat) => {
        setEditId(cat.id);
        setFormState({
            id: cat.id,
            name: cat.name,
            useInYearlyCharts: cat.useInYearlyCharts,
            level: cat.level ?? ""
        });
        setShowModal(true);
    };

    const handleChange = (e) => {
        const {name, value, type, checked} = e.target;
        setFormState(prev => ({...prev, [name]: type === "checkbox" ? checked : value}));
    };

    const handleSave = async () => {
        const payload = {
            ...formState,
            level: formState.level === "" ? null : Number(formState.level)
        };

        if (editId) {
            await fetch(`/budget/categories/${editId}`, {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(payload)
            });
        } else {
            await fetch("/budget/categories", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(payload)
            });
        }
        setShowModal(false);
        loadCategories();
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Usunąć kategorię?")) return;
        await fetch(`/budget/categories/${id}`, {method: "DELETE"});
        loadCategories();
    };

    const levelName = (levelInt) => {
        const found = levels.find(l => String(l.level) === String(levelInt));
        return found ? found.name : (levelInt ?? "—");
    };

    const handleLevelChange = (e) => {
        const {name, value} = e.target;
        setLevelFormState(prev => ({...prev, [name]: value}));
    };

    const handleLevelSave = async () => {
        await fetch("/budget/categories/levels", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(levelFormState)
        });
        setShowLevelModal(false);
        setLevelFormState(EMPTY_LEVEL_FORM);
        loadLevels();
    };

    return (
        <>
            <Row>
                <Col>


                    <Row className="mb-3 mt-2">
                        <Col>
                            <h4>Kategorie</h4>
                        </Col>
                        <Col className="text-end">
                            <Button size="sm" onClick={openAdd}>
                                <PlusCircle/> Dodaj kategorię
                            </Button>
                        </Col>
                    </Row>
                    <Table striped hover size="sm">
                        <thead>
                        <tr>
                            <th>Nazwa</th>
                            <th>Poziom</th>
                            <th>Wykresy roczne</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        {categories.map(cat => (
                            <tr key={cat.id}>
                                <td>{cat.name}</td>
                                <td>{levelName(cat.level)}</td>
                                <td>{cat.useInYearlyCharts ? "✓" : "✗"}</td>
                                <td className="text-end">
                                    <Button size="sm" variant="outline-secondary" className="me-1"
                                            onClick={() => openEdit(cat)}>
                                        <PencilSquare/>
                                    </Button>
                                    <Button size="sm" variant="outline-danger"
                                            onClick={() => handleDelete(cat.id)}>
                                        <Trash/>
                                    </Button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </Table>

                    <Modal show={showModal} onHide={() => setShowModal(false)}>
                        <Modal.Header closeButton>
                            <Modal.Title>{editId ? "Edytuj kategorię" : "Nowa kategoria"}</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>
                            <Form>
                                <Form.Group className="mb-3">
                                    <Form.Label>Nazwa</Form.Label>
                                    <Form.Control name="name" value={formState.name}
                                                  onChange={handleChange} required/>
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Poziom kategorii</Form.Label>
                                    <Form.Select name="level" value={formState.level} onChange={handleChange}>
                                        <option value="">— brak —</option>
                                        {levels.map(l => (
                                            <option key={l.id} value={l.level}>{l.name}</option>
                                        ))}
                                    </Form.Select>
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Check type="switch" name="useInYearlyCharts"
                                                id="useInYearlyCharts"
                                                label="Używaj w wykresach rocznych"
                                                checked={formState.useInYearlyCharts}
                                                onChange={handleChange}/>
                                </Form.Group>
                            </Form>
                        </Modal.Body>
                        <Modal.Footer>
                            <Button variant="secondary" onClick={() => setShowModal(false)}>Anuluj</Button>
                            <Button variant="primary" onClick={handleSave}>Zapisz</Button>
                        </Modal.Footer>
                    </Modal>
                </Col>
                <Col>
                    <Row className="mb-3 mt-4">
                        <Col>
                            <h4>Poziomy kategorii</h4>
                        </Col>
                        <Col className="text-end">
                            <Button size="sm" onClick={() => {
                                setLevelFormState(EMPTY_LEVEL_FORM);
                                setShowLevelModal(true);
                            }}>
                                <PlusCircle/> Dodaj poziom
                            </Button>
                        </Col>
                    </Row>
                    <Table striped hover size="sm">
                        <thead>
                        <tr>
                            <th>Numer</th>
                            <th>Nazwa</th>
                        </tr>
                        </thead>
                        <tbody>
                        {levels.map(l => (
                            <tr key={l.id}>
                                <td>{l.level}</td>
                                <td>{l.name}</td>
                            </tr>
                        ))}
                        </tbody>
                    </Table>

                    <Modal show={showLevelModal} onHide={() => setShowLevelModal(false)}>
                        <Modal.Header closeButton>
                            <Modal.Title>Nowy poziom</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>
                            <Form>
                                <Form.Group className="mb-3">
                                    <Form.Label>Numer poziomu</Form.Label>
                                    <Form.Control type="number" name="level" value={levelFormState.level}
                                                  onChange={handleLevelChange} required/>
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Nazwa</Form.Label>
                                    <Form.Control name="name" value={levelFormState.name}
                                                  onChange={handleLevelChange} required/>
                                </Form.Group>
                            </Form>
                        </Modal.Body>
                        <Modal.Footer>
                            <Button variant="secondary" onClick={() => setShowLevelModal(false)}>Anuluj</Button>
                            <Button variant="primary" onClick={handleLevelSave}>Zapisz</Button>
                        </Modal.Footer>
                    </Modal>
                </Col>
            </Row>
        </>
    );
};

const CategoryAdmin = () => <Main body={<CategoryAdminContent/>}/>;

export default CategoryAdmin;
