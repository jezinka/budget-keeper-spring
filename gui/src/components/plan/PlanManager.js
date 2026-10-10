import React, {useCallback, useEffect, useMemo, useState} from "react";
import {Button, Col, Form, Modal, Row, Table} from "react-bootstrap";
import Main from "../main/Main";
import MonthYearFilter from "../monthlyView/MonthYearFilter";
import PlanImport from "./PlanImport";
import PlanPieChart from "./PlanPieChart";
import {formatNumber, getMonthName} from "../../Utils";
import {ArrowCounterclockwise, ArrowRepeat, Pencil, Plus, ShieldExclamation, Trash} from "react-bootstrap-icons";
import {useLocation} from "react-router-dom";

const PlanManager = () => {
    const now = new Date();
    const location = useLocation();
    const [year, setYear] = useState(now.getFullYear());
    const [month, setMonth] = useState(now.getMonth() + 1);
    const [summary, setSummary] = useState(null);
    const [plannedExpenseForm, setPlannedExpenseForm] = useState({amount: "", name: "", dueDate: ""});
    const [selectedPlannedExpense, setSelectedPlannedExpense] = useState(null);
    const [unplannedFilters, setUnplannedFilters] = useState({description: "", category: ""});
    const [showImport, setShowImport] = useState(false);
    const [editingPlannedExpense, setEditingPlannedExpense] = useState(null);
    const [selectedExpenseIds, setSelectedExpenseIds] = useState([]);
    const [selectedTargetId, setSelectedTargetId] = useState("");
    const [showRecurring, setShowRecurring] = useState(false);
    const [recurringPayments, setRecurringPayments] = useState([]);
    const [recurringPaymentForm, setRecurringPaymentForm] = useState({name: "", amount: "", dueDay: ""});
    const [showExcluded, setShowExcluded] = useState(false);

    const loadSummary = useCallback(async () => {
        const response = await fetch(`/budget/plans/summary?year=${year}&month=${month}`);
        if (response.ok) setSummary(await response.json());
    }, [year, month]);

    useEffect(() => {
        loadSummary();
    }, [loadSummary]);

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const queryYear = Number(params.get("year"));
        const queryMonth = Number(params.get("month"));

        if (Number.isInteger(queryYear) && queryYear > 0) {
            setYear(prev => prev === queryYear ? prev : queryYear);
        }
        if (Number.isInteger(queryMonth) && queryMonth >= 1 && queryMonth <= 12) {
            setMonth(prev => prev === queryMonth ? prev : queryMonth);
        }
    }, [location.search]);

    useEffect(() => {
        loadRecurringPayments();
    }, []);

    async function loadRecurringPayments() {
        const response = await fetch("/budget/recurringPlannedExpenses");
        if (response.ok) setRecurringPayments(await response.json());
    }

    function updateForm(setForm, event) {
        const {name, value} = event.target;
        setForm(form => ({...form, [name]: value}));
    }

    async function createPlan() {
        const response = await fetch("/budget/plans", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({year, month})
        });
        if (!response.ok) return alert(await response.text());
        loadSummary();
    }

    async function addPlannedExpense(event) {
        event.preventDefault();
        const response = await fetch("/budget/plannedExpenses", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                ...plannedExpenseForm,
                planId: summary.plan.id,
                amount: Number(plannedExpenseForm.amount),
                dueDate: plannedExpenseForm.dueDate || null
            })
        });
        if (!response.ok) return alert(await response.text());
        setPlannedExpenseForm({amount: "", name: "", dueDate: ""});
        loadSummary();
    }

    async function deletePlannedExpense(id) {
        if (!window.confirm("Usunąć planowany wydatek?")) return;
        const response = await fetch(`/budget/plannedExpenses/${id}`, {method: "DELETE"});
        if (!response.ok) return alert(await response.text());
        loadSummary();
    }

    async function updatePlannedExpense(event) {
        event.preventDefault();
        const response = await fetch(`/budget/plannedExpenses/${editingPlannedExpense.id}`, {
            method: "PUT",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                ...editingPlannedExpense,
                amount: Number(editingPlannedExpense.amount),
                dueDate: editingPlannedExpense.dueDate || null
            })
        });
        if (!response.ok) return alert(await response.text());
        setEditingPlannedExpense(null);
        loadSummary();
    }

    async function updatePaid(id, paid) {
        const response = await fetch(`/budget/plannedExpenses/${id}/paid?paid=${paid}`, {method: "PATCH"});
        if (!response.ok) return alert(await response.text());
        loadSummary();
    }

    async function addRecurringPayment(event) {
        event.preventDefault();
        const response = await fetch("/budget/recurringPlannedExpenses", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                ...recurringPaymentForm,
                amount: Number(recurringPaymentForm.amount),
                dueDay: Number(recurringPaymentForm.dueDay)
            })
        });
        if (!response.ok) return alert(await response.text());
        setRecurringPaymentForm({name: "", amount: "", dueDay: ""});
        loadRecurringPayments();
    }

    async function deleteRecurringPayment(id) {
        if (!window.confirm("Usunąć płatność cykliczną?")) return;
        const response = await fetch(`/budget/recurringPlannedExpenses/${id}`, {method: "DELETE"});
        if (!response.ok) return alert(await response.text());
        loadRecurringPayments();
    }

    async function applyRecurringPayments() {
        const response = await fetch(`/budget/plans/${summary.plan.id}/recurringPlannedExpenses`, {method: "POST"});
        if (!response.ok) return alert(await response.text());
        loadSummary();
    }

    async function convertPlannedExpenseToRecurring(id) {
        const response = await fetch(`/budget/plannedExpenses/${id}/recurring`, {method: "POST"});
        if (!response.ok) return alert(await response.text());
        loadRecurringPayments();
        loadSummary();
    }

    async function convertUnplannedExpenseToRecurring(id) {
        const response = await fetch(`/budget/plans/${summary.plan.id}/unplannedExpenses/${id}/recurring`, {method: "POST"});
        if (!response.ok) return alert(await response.text());
        loadRecurringPayments();
        loadSummary();
    }

    async function updateExcludedFromPlan(id, excluded) {
        const response = await fetch(`/budget/expenses/${id}/excludedFromPlan?excluded=${excluded}`, {method: "PATCH"});
        if (!response.ok) return alert(await response.text());
        setSelectedExpenseIds(selectedExpenseIds.filter(expenseId => expenseId !== id));
        loadSummary();
    }

    async function markAsPlanned(expenseId, plannedExpenseId) {
        const url = plannedExpenseId ?
            `/budget/plannedExpenses/${plannedExpenseId}/expenses/${expenseId}` :
            `/budget/plans/${summary.plan.id}/plannedExpenses/fromExpense/${expenseId}`;
        const response = await fetch(url, {method: "POST"});
        if (!response.ok) return alert(await response.text());
        loadSummary();
    }

    async function assignSelectedExpenses() {
        const response = await fetch(`/budget/plannedExpenses/${selectedTargetId}/expenses`, {
            method: "POST", headers: {"Content-Type": "application/json"}, body: JSON.stringify(selectedExpenseIds)
        });
        if (!response.ok) return alert(await response.text());
        setSelectedExpenseIds([]);
        setSelectedTargetId("");
        loadSummary();
    }

    async function createPlannedExpenseFromSelectedExpenses() {
        const response = await fetch(`/budget/plans/${summary.plan.id}/plannedExpenses/fromExpenses`, {
            method: "POST", headers: {"Content-Type": "application/json"}, body: JSON.stringify(selectedExpenseIds)
        });
        if (!response.ok) return alert(await response.text());
        setSelectedExpenseIds([]);
        loadSummary();
    }

    async function unassignExpense(expenseId) {
        const response = await fetch(`/budget/plannedExpenses/${selectedPlannedExpense.id}/expenses/${expenseId}`, {method: "DELETE"});
        if (!response.ok) return alert(await response.text());
        setSelectedPlannedExpense(null);
        loadSummary();
    }

    const unplannedExpenses = useMemo(() => summary?.unplannedExpenses.filter(expense =>
        Number(expense.amount) < 0 &&
        (showExcluded || !expense.excludedFromPlan) &&
        (expense.description || expense.title || "").toLowerCase().includes(unplannedFilters.description.toLowerCase()) &&
        (expense.categoryName || "").toLowerCase().includes(unplannedFilters.category.toLowerCase())
    ) || [], [summary, unplannedFilters, showExcluded]);

    const selectableUnplannedExpenses = unplannedExpenses.filter(expense => !expense.excludedFromPlan);

    const categories = useMemo(() => [...(summary?.categories || [])].sort(
        (first, second) => Number(first.difference) - Number(second.difference)
    ), [summary]);

    const plannedExpenseRows = useMemo(() => (summary?.plannedExpenses || []).map(item => {
        const spent = summary.plannedExpenseTransactions
            .filter(expense => expense.plannedExpenseId === item.id)
            .reduce((sum, expense) => sum + Math.abs(Number(expense.amount)), 0);
        return {...item, spent, difference: Number(item.amount) - spent};
    }).sort((first, second) => Number(first.paid) - Number(second.paid) ||
        (first.dueDate || "9999-12-31").localeCompare(second.dueDate || "9999-12-31")), [summary]);

    const sortedPlannedExpenses = useMemo(() => [...(summary?.plannedExpenses || [])].sort(
        (first, second) => (first.name || "Bez nazwy").localeCompare(second.name || "Bez nazwy", "pl", {
            sensitivity: "base"
        })
    ), [summary]);

    const plannedExpenseTotals = plannedExpenseRows.reduce((totals, item) => ({
        planned: totals.planned + Number(item.amount),
        spent: totals.spent + item.spent,
        difference: totals.difference + item.difference
    }), {planned: 0, spent: 0, difference: 0});

    const unplannedTotal = unplannedExpenses.filter(expense => !expense.excludedFromPlan)
        .reduce((total, expense) => total + Math.abs(Number(expense.amount)), 0);
    const chartAmount = name => summary?.plannedVsUnplanned.find(item => item.name === name)?.amount || 0;
    const categoryTotals = categories.reduce((totals, category) => ({
        planned: totals.planned + Number(category.plannedAmount),
        actual: totals.actual + Number(category.actualAmount),
        difference: totals.difference + Number(category.difference)
    }), {planned: 0, actual: 0, difference: 0});

    const categoryRowStyle = difference => {
        if (Number(difference) >= 0) return {backgroundColor: "#d1e7dd"};
        const largestDeficit = Math.min(...categories.map(category => Number(category.difference)));
        const level = Math.ceil(Math.abs(Number(difference) / largestDeficit) * 4);
        return {backgroundColor: ["#f8d7da", "#f5c2c7", "#ffe5b4", "#fff3cd"][4 - level]};
    };

    const body = <Col sm={11}>
        <h2>Plan na {getMonthName(month, "long")} {year}</h2>
        <MonthYearFilter year={year} month={month} onYearChange={setYear} onMonthChange={setMonth}/>

        {!summary?.plan ? <Button onClick={createPlan}>Utwórz plan</Button> : <>
            <Form className="row g-2 mb-4" onSubmit={addPlannedExpense}>
                <Col sm={2}><Form.Control required min="0.01" step="0.01" type="number" placeholder="Kwota"
                                          name="amount" value={plannedExpenseForm.amount}
                                          onChange={e => updateForm(setPlannedExpenseForm, e)}/></Col>
                <Col sm={3}><Form.Control placeholder="Nazwa" name="name" value={plannedExpenseForm.name}
                                          onChange={e => updateForm(setPlannedExpenseForm, e)}/></Col>
                <Col sm={2}><Form.Control aria-label="Termin planowanego wydatku" type="date" name="dueDate" value={plannedExpenseForm.dueDate}
                                          onChange={e => updateForm(setPlannedExpenseForm, e)}/></Col>
                <Col sm={2}><Button type="submit">Dodaj do planu</Button></Col>
                <Col sm={2}><Button type="button" variant="outline-secondary" onClick={() => setShowImport(true)}>Import
                    CSV</Button></Col>
                <Col sm={2}><Button type="button" variant="outline-secondary"
                                    onClick={() => setShowRecurring(true)}>Cykliczne</Button></Col>
                <Col sm={2}><Button type="button" variant="outline-primary" onClick={applyRecurringPayments}>Dodaj
                    cykliczne</Button></Col>
            </Form>

            <Row>
                <Col md={7}>
                    <h4>Zaplanowane wydatki</h4>
                    <Table responsive striped bordered size="sm">
                        <thead>
                        <tr>
                            <th>Opłacone</th>
                            <th>Termin</th>
                            <th>Nazwa</th>
                            <th>Plan</th>
                            <th>Wydane</th>
                            <th>Różnica</th>
                            <th/>
                        </tr>
                        </thead>
                        <tbody>{plannedExpenseRows.map(item => {
                            return <tr key={item.id} className={item.paid ? "table-secondary" : ""}>
                                <td><Form.Check checked={item.paid}
                                                onChange={e => updatePaid(item.id, e.target.checked)}/></td>
                                <td>{item.dueDate || "-"}</td>
                                <td>{item.name}</td>
                                <td>{formatNumber(item.amount)}</td>
                                <td
                                    onClick={() => setSelectedPlannedExpense({
                                        id: item.id, expenses: summary.plannedExpenseTransactions.filter(
                                            expense => expense.plannedExpenseId === item.id)
                                    })}>{formatNumber(item.spent)}</td>
                                <td>{formatNumber(item.difference)}</td>
                                <td><Button className="me-1" size="sm" variant="outline-primary"
                                            onClick={() => setEditingPlannedExpense({
                                                id: item.id,
                                                planId: item.planId,
                                                amount: item.amount,
                                                name: item.name,
                                                dueDate: item.dueDate,
                                                paid: item.paid
                                            })}><Pencil/></Button>
                                    {!item.recurringPaymentId &&
                                        <Button className="me-1" size="sm" variant="outline-secondary"
                                                title="Utwórz płatność cykliczną"
                                                onClick={() => convertPlannedExpenseToRecurring(item.id)}><ArrowRepeat/></Button>}
                                    <Button size="sm" variant="outline-danger"
                                            onClick={() => deletePlannedExpense(item.id)}><Trash/></Button></td>
                            </tr>;
                        })}</tbody>
                        <tfoot>
                        <tr>
                            <th colSpan={3}>Razem</th>
                            <th>{formatNumber(plannedExpenseTotals.planned)}</th>
                            <th>{formatNumber(plannedExpenseTotals.spent)}</th>
                            <th>{formatNumber(plannedExpenseTotals.difference)}</th>
                            <th/>
                        </tr>
                        </tfoot>
                    </Table>

                    <h4>Poza planem</h4>
                    <Form.Check className="mb-2" label="Pokaż wykluczone" checked={showExcluded}
                                onChange={e => setShowExcluded(e.target.checked)}/>
                    <Table responsive striped bordered size="sm">
                        <thead>
                        <tr>
                            <th><Form.Check
                                checked={selectableUnplannedExpenses.length > 0 && selectableUnplannedExpenses.every(
                                    expense => selectedExpenseIds.includes(expense.id))}
                                onChange={e => setSelectedExpenseIds(e.target.checked ?
                                    [...new Set([...selectedExpenseIds, ...selectableUnplannedExpenses.map(expense => expense.id)])] :
                                    selectedExpenseIds.filter(id => !selectableUnplannedExpenses.some(expense => expense.id === id)))}/>
                            </th>
                            <th style={{width: "10%"}}>Data</th>
                            <th style={{width: "45%"}}>Opis</th>
                            <th>Kategoria</th>
                            <th>Kwota</th>
                            <th/>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td/>
                            <td/>
                            <td><Form.Control size="sm" placeholder="Filtruj opis" value={unplannedFilters.description}
                                              name="description" onChange={e => updateForm(setUnplannedFilters, e)}/></td>
                            <td><Form.Control size="sm" placeholder="Filtruj kategorię"
                                              value={unplannedFilters.category}
                                              name="category" onChange={e => updateForm(setUnplannedFilters, e)}/></td>
                            <td/>
                            <td/>
                        </tr>
                        {unplannedExpenses.map(expense =>
                            <tr key={expense.id} className={expense.excludedFromPlan ? "table-secondary" : ""}>
                                <td>{!expense.excludedFromPlan &&
                                    <Form.Check checked={selectedExpenseIds.includes(expense.id)}
                                                onChange={e => setSelectedExpenseIds(e.target.checked ? [...selectedExpenseIds, expense.id] :
                                                    selectedExpenseIds.filter(id => id !== expense.id))}/>}</td>
                                <td>{expense.transactionDate}</td>
                                <td>{expense.description || expense.title}</td>
                                <td>{expense.categoryName}</td>
                                <td>{formatNumber(Math.abs(Number(expense.amount)))}</td>
                                <td>
                                    {expense.excludedFromPlan ? <Button size="sm" variant="outline-secondary"
                                                                        onClick={() => updateExcludedFromPlan(expense.id, false)}><ArrowCounterclockwise/></Button> :
                                        <div className="d-flex gap-1">
                                            {summary.plannedExpenses.length > 0 &&
                                                <Form.Select size="sm" defaultValue=""
                                                             onChange={e => e.target.value && markAsPlanned(expense.id, e.target.value)}>
                                                    <option value="">Przypisz do...</option>
                                                    {sortedPlannedExpenses
                                                        .map(item => <option key={item.id}
                                                                             value={item.id}>{item.name || "Bez nazwy"}</option>)}
                                                </Form.Select>}
                                            <Button size="sm" onClick={() => markAsPlanned(expense.id)}><Plus/></Button>
                                            <Button size="sm" variant="outline-secondary"
                                                    title="Utwórz płatność cykliczną"
                                                    onClick={() => convertUnplannedExpenseToRecurring(expense.id)}><ArrowRepeat/></Button>
                                            <Button size="sm" variant="outline-secondary"
                                                    onClick={() => updateExcludedFromPlan(expense.id, true)}><ShieldExclamation/></Button>
                                        </div>}
                                </td>
                            </tr>)}</tbody>
                        <tfoot>
                        <tr>
                            <th colSpan={4}>Razem</th>
                            <th>{formatNumber(unplannedTotal)}</th>
                            <th/>
                        </tr>
                        </tfoot>
                    </Table>
                    {selectedExpenseIds.length > 0 && <div className="d-flex gap-1 mb-3">
                        <Form.Select size="sm" value={selectedTargetId}
                                     onChange={e => setSelectedTargetId(e.target.value)}>
                            <option value="">Przypisz do...</option>
                            {sortedPlannedExpenses.map(item => <option key={item.id}
                                                                        value={item.id}>{item.name || "Bez nazwy"}</option>)}
                        </Form.Select>
                        <Button size="sm" disabled={!selectedTargetId} onClick={assignSelectedExpenses}>Przypisz
                            wszystkie do planowanego wydatku</Button>
                        <Button size="sm" variant="outline-primary" onClick={createPlannedExpenseFromSelectedExpenses}>Utwórz
                            nowy z przypisanymi wszystkimi zaznaczonymi</Button>
                    </div>}
                </Col>
                <Col md={5}>
                    <h4>Plan a rzeczywistość</h4>
                    <p>Do zapłaty: {formatNumber(summary.remainingPlannedAmount)} ({summary.remainingPlannedCount}) |
                        Opłacone: {formatNumber(summary.paidPlannedAmount)} ({summary.paidPlannedCount})</p>
                    <p>Wydane w planie: {formatNumber(chartAmount("Zaplanowane"))} |
                        Przekroczone: {formatNumber(chartAmount("Przekroczony plan"))} | Poza
                        planem: {formatNumber(chartAmount("Niezaplanowane"))}</p>
                    <PlanPieChart data={summary.plannedVsUnplanned}/>
                    <h4>Kategorie</h4>
                    <Table responsive bordered size="sm">
                        <thead>
                        <tr>
                            <th>Kategoria</th>
                            <th>Zaplanowane</th>
                            <th>Wydane</th>
                            <th>Różnica</th>
                        </tr>
                        </thead>
                        <tbody>{categories.map(category => {
                            const style = categoryRowStyle(category.difference);
                            return <tr key={category.categoryId}>
                                <td style={style}>{category.categoryName}</td>
                                <td style={style}>{formatNumber(category.plannedAmount)}</td>
                                <td style={style}>{formatNumber(category.actualAmount)}</td>
                                <td style={style}>{formatNumber(category.difference)}</td>
                            </tr>;
                        })}</tbody>
                        <tfoot>
                        <tr>
                            <th>Razem</th>
                            <th>{formatNumber(categoryTotals.planned)}</th>
                            <th>{formatNumber(categoryTotals.actual)}</th>
                            <th>{formatNumber(categoryTotals.difference)}</th>
                        </tr>
                        </tfoot>
                    </Table>
                </Col>
            </Row>
        </>}
        <Modal show={showImport} onHide={() => setShowImport(false)}>
            <Modal.Header closeButton><Modal.Title>Import planowanych wydatków</Modal.Title></Modal.Header>
            <Modal.Body><p>Format CSV: <code>Kategoria,Kwota</code> lub <code>Kategoria,Kwota,Termin</code></p>
                <PlanImport uploadUrl={`/budget/plans/${summary?.plan?.id}/upload`} closeHandler={() => {
                    setShowImport(false);
                    loadSummary();
                }}/></Modal.Body>
        </Modal>
        <Modal show={showRecurring} onHide={() => setShowRecurring(false)}>
            <Modal.Header closeButton><Modal.Title>Płatności cykliczne</Modal.Title></Modal.Header>
            <Modal.Body>
                <Form className="row g-2 mb-3" onSubmit={addRecurringPayment}>
                    <Col sm={5}><Form.Control required placeholder="Nazwa" name="name"
                                              value={recurringPaymentForm.name}
                                              onChange={e => updateForm(setRecurringPaymentForm, e)}/></Col>
                    <Col sm={3}><Form.Control required min="0.01" step="0.01" type="number" placeholder="Kwota"
                                              name="amount" value={recurringPaymentForm.amount}
                                              onChange={e => updateForm(setRecurringPaymentForm, e)}/></Col>
                    <Col sm={2}><Form.Control required min="1" max="31" type="number" placeholder="Dzień"
                                              name="dueDay" value={recurringPaymentForm.dueDay}
                                              onChange={e => updateForm(setRecurringPaymentForm, e)}/></Col>
                    <Col sm={2}><Button type="submit"><Plus/></Button></Col>
                </Form>
                <Table responsive striped size="sm">
                    <thead>
                    <tr>
                        <th>Nazwa</th>
                        <th>Kwota</th>
                        <th>Dzień</th>
                        <th/>
                    </tr>
                    </thead>
                    <tbody>{recurringPayments.map(payment => <tr key={payment.id}>
                        <td>{payment.name}</td>
                        <td>{formatNumber(payment.amount)}</td>
                        <td>{payment.dueDay}.</td>
                        <td><Button size="sm" variant="outline-danger"
                                    onClick={() => deleteRecurringPayment(payment.id)}><Trash/></Button></td>
                    </tr>)}</tbody>
                </Table>
            </Modal.Body>
        </Modal>
        <Modal show={editingPlannedExpense !== null} onHide={() => setEditingPlannedExpense(null)}>
            <Form onSubmit={updatePlannedExpense}>
                <Modal.Header closeButton><Modal.Title>Edytuj planowany wydatek</Modal.Title></Modal.Header>
                <Modal.Body>
                    <Form.Group className="mb-3"><Form.Label column>Nazwa</Form.Label>
                        <Form.Control name="name" value={editingPlannedExpense?.name || ""}
                                      onChange={e => updateForm(setEditingPlannedExpense, e)}/></Form.Group>
                    <Form.Group><Form.Label column>Kwota</Form.Label>
                        <Form.Control min="0.01" step="0.01" type="number" name="amount"
                                      value={editingPlannedExpense?.amount || ""}
                                      onChange={e => updateForm(setEditingPlannedExpense, e)}/></Form.Group>
                    <Form.Group className="mt-3"><Form.Label column>Termin</Form.Label>
                        <Form.Control type="date" name="dueDate" value={editingPlannedExpense?.dueDate || ""}
                                      onChange={e => updateForm(setEditingPlannedExpense, e)}/></Form.Group>
                </Modal.Body>
                <Modal.Footer><Button variant="secondary" onClick={() => setEditingPlannedExpense(null)}>Anuluj</Button>
                    <Button type="submit">Zapisz</Button></Modal.Footer>
            </Form>
        </Modal>
        <Modal show={selectedPlannedExpense !== null} onHide={() => setSelectedPlannedExpense(null)}>
            <Modal.Header closeButton><Modal.Title>Przypisane wydatki</Modal.Title></Modal.Header>
            <Modal.Body>
                {selectedPlannedExpense?.expenses.length === 0 ? <p>Brak przypisanych wydatków.</p> :
                    <Table responsive striped size="sm">
                        <tbody>{selectedPlannedExpense?.expenses.map(expense =>
                            <tr key={expense.id}>
                                <td style={{width: "20%"}}>{expense.transactionDate}</td>
                                <td>{expense.description.substring(0, 80) || expense.title.substring(0, 80)}</td>
                                <td>{formatNumber(Math.abs(Number(expense.amount)))}</td>
                                <td><Button size="sm" variant="outline-danger"
                                            onClick={() => unassignExpense(expense.id)}><Trash/></Button></td>
                            </tr>)}</tbody>
                    </Table>}
            </Modal.Body>
        </Modal>
    </Col>;

    return <Main body={body}/>;
};

export default PlanManager;
