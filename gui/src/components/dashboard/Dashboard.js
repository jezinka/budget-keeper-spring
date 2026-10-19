import React, {useEffect, useState} from "react";
import {Badge, Button, Card, Col, ListGroup, Row} from "react-bootstrap";
import Main from "../main/Main";
import {formatNumber, UNKNOWN_CATEGORY} from "../../Utils";
import ExpensePieChart from "../charts/ExpensePieChart";
import PlanPieChart from "../plan/PlanPieChart";

const today = () => new Intl.DateTimeFormat('sv-SE').format(new Date());

const Stat = ({label, value, variant}) => (
    <Col>
        <Card className="text-center">
            <Card.Body>
                <Card.Subtitle className="text-muted mb-2">{label}</Card.Subtitle>
                <Card.Title className={variant ? `text-${variant}` : ""}>{value}</Card.Title>
            </Card.Body>
        </Card>
    </Col>
);

const Dashboard = () => {
    const [summary, setSummary] = useState(null);
    const [moneyAmount, setMoneyAmount] = useState({});
    const [transactions, setTransactions] = useState([]);
    const [levelExpenses, setLevelExpenses] = useState([]);
    const [topExpenses, setTopExpenses] = useState([]);

    const now = new Date();
    const period = `year=${now.getFullYear()}&month=${now.getMonth() + 1}`;

    async function loadSummary() {
        const response = await fetch(`/budget/plans/summary?${period}`);
        setSummary(response.ok ? await response.json() : false);
    }

    async function loadMoneyAmount() {
        const response = await fetch('/budget/moneyAmount');
        if (response.ok) setMoneyAmount(await response.json());
    }

    async function loadTransactions() {
        const response = await fetch('/budget/expenses/currentMonth');
        if (response.ok) setTransactions(await response.json());
    }

    async function loadChart(url, setData) {
        const response = await fetch(url);
        if (response.ok) setData(await response.json());
    }

    useEffect(() => {
        loadSummary();
        loadMoneyAmount();
        loadTransactions();
        loadChart(`/budget/expenses/categoryLevelExpensesForMonth?${period}`, setLevelExpenses);
        loadChart(`/budget/expenses/topExpensesForMonth?${period}`, setTopExpenses);
    }, []);

    async function markPaid(id) {
        const response = await fetch(`/budget/plannedExpenses/${id}/paid?paid=true`, {method: 'PATCH'});
        if (response.ok) loadSummary();
    }

    if (summary === null) return <Main body={<p>Ładowanie…</p>}/>;
    if (summary === false) return <Main body={<p className="text-danger">Nie udało się pobrać podsumowania planu.</p>}/>;

    // Koperty (singlePayment = false) wydaje się wieloma transakcjami, więc nie są przelewem do zrobienia.
    const toPay = summary.plannedExpenses
        .filter(p => !p.paid && p.singlePayment !== false)
        .sort((a, b) => (a.dueDate ?? '9999').localeCompare(b.dueDate ?? '9999'));
    const overruns = summary.categories.filter(c => c.plannedAmount > 0 && c.underPlanned);
    const uncategorized = transactions.filter(t => t.categoryId == null || t.categoryId === UNKNOWN_CATEGORY).length;
    const unsettledTransfers = transactions.filter(t => t.transferSettled === false).length;
    const freeFunds = (Number(moneyAmount.accountBalance) || 0) - Number(summary.remainingPlannedAmount);

    const actions = [
        summary.plan === null && {text: 'Brak planu na ten miesiąc', href: '/budget/gui/plan', variant: 'danger'},
        uncategorized > 0 && {text: `Transakcje bez kategorii: ${uncategorized}`, href: '/budget/gui/currentMonth', variant: 'warning'},
        unsettledTransfers > 0 && {text: `Nierozliczone przelewy: ${unsettledTransfers}`, href: '/budget/gui/currentMonth', variant: 'warning'},
        ...overruns.map(c => ({
            text: `Przekroczony plan: ${c.categoryName} o ${formatNumber(-c.difference)}`,
            href: '/budget/gui/plan',
            variant: 'danger'
        })),
    ].filter(Boolean);

    const body = <Col>
        <Row className="mb-3">
            <Stat label="Stan konta" value={formatNumber(moneyAmount.accountBalance)}/>
            <Stat label="Wolne środki" value={formatNumber(freeFunds)} variant={freeFunds < 0 ? 'danger' : 'success'}/>
            <Stat label="Wydatki w miesiącu" value={formatNumber(moneyAmount.expenses)}/>
            <Stat label={`Zostało w planie (${summary.remainingPlannedCount})`} value={formatNumber(summary.remainingPlannedAmount)}/>
            <Stat label="Poza planem" value={formatNumber(summary.unplannedExpenseAmount)}
                  variant={summary.unplannedExpenseAmount > 0 ? 'warning' : undefined}/>
        </Row>
        <Row className="mb-3">
            {levelExpenses.length > 0 && <Col sm={4}>
                <h5>Wydatki wg poziomów</h5>
                <ExpensePieChart data={levelExpenses} hueStep={52} legend/>
            </Col>}
            {topExpenses.length > 0 && <Col sm={4}>
                <h5>Największe wydatki</h5>
                <ExpensePieChart data={topExpenses}/>
            </Col>}
            {summary.plannedVsUnplanned?.length > 0 && <Col sm={4}>
                <h5>Zaplanowane a poza planem</h5>
                <PlanPieChart data={summary.plannedVsUnplanned}/>
            </Col>}
        </Row>
        <Row>
            <Col sm={6}>
                <h5>Do zrobienia</h5>
                <ListGroup>
                    {actions.length === 0 && <ListGroup.Item variant="success">Wszystko ogarnięte 🎉</ListGroup.Item>}
                    {actions.map(a => (
                        <ListGroup.Item key={a.text} action href={a.href} variant={a.variant}>{a.text}</ListGroup.Item>
                    ))}
                </ListGroup>
            </Col>
            <Col sm={6}>
                <h5>Do zapłacenia</h5>
                <ListGroup>
                    {toPay.length === 0 && <ListGroup.Item>Nic nie zostało do zapłacenia</ListGroup.Item>}
                    {toPay.map(p => (
                        <ListGroup.Item key={p.id} className="d-flex justify-content-between align-items-center">
                            <span>
                                {p.name}
                                {p.dueDate && <Badge bg={p.dueDate < today() ? 'danger' : 'secondary'} className="ms-2">
                                    {p.dueDate}
                                </Badge>}
                            </span>
                            <span>
                                {formatNumber(p.amount)}
                                <Button size="sm" variant="outline-success" className="ms-2"
                                        onClick={() => markPaid(p.id)}>Zapłacone</Button>
                            </span>
                        </ListGroup.Item>
                    ))}
                </ListGroup>
            </Col>
        </Row>
    </Col>;

    return <Main body={body}/>;
}

export default Dashboard;
