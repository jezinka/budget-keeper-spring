import React, {useEffect, useMemo, useState} from "react";
import {Alert, Button, Col, Row, Table} from "react-bootstrap";
import {useNavigate} from "react-router-dom";
import {formatNumber, getMonthName} from "../../Utils";

const toNumber = value => Number(value ?? 0);

export const PlansAdminContent = () => {
    const [plans, setPlans] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    async function loadPlans() {
        setLoading(true);
        setError("");

        try {
            const response = await fetch("/budget/plans");
            if (!response.ok) {
                setError(await response.text());
                return;
            }

            const sourcePlans = await response.json();
            const rows = await Promise.all(sourcePlans.map(async plan => {
                const summaryResponse = await fetch(`/budget/plans/summary?year=${plan.year}&month=${plan.month}`);
                if (!summaryResponse.ok) {
                    return {
                        ...plan,
                        plannedExpenseAmount: 0,
                        unplannedExpenseAmount: 0,
                        paidPlannedAmount: 0,
                        remainingPlannedAmount: 0,
                        plannedCount: 0,
                        unplannedCount: 0
                    };
                }

                const summary = await summaryResponse.json();
                return {
                    ...plan,
                    plannedExpenseAmount: toNumber(summary.plannedExpenseAmount),
                    unplannedExpenseAmount: toNumber(summary.unplannedExpenseAmount),
                    paidPlannedAmount: toNumber(summary.paidPlannedAmount),
                    remainingPlannedAmount: toNumber(summary.remainingPlannedAmount),
                    plannedCount: summary.plannedExpenses?.length ?? 0,
                    unplannedCount: summary.unplannedExpenses?.length ?? 0
                };
            }));

            rows.sort((first, second) => second.year - first.year || second.month - first.month);
            setPlans(rows);
        } catch (e) {
            setError(e.message || "Nie udało się pobrać planów");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadPlans();
    }, []);

    const totals = useMemo(() => plans.reduce((acc, plan) => ({
        planned: acc.planned + toNumber(plan.plannedExpenseAmount),
        unplanned: acc.unplanned + toNumber(plan.unplannedExpenseAmount),
        paid: acc.paid + toNumber(plan.paidPlannedAmount),
        remaining: acc.remaining + toNumber(plan.remainingPlannedAmount)
    }), {planned: 0, unplanned: 0, paid: 0, remaining: 0}), [plans]);

    const openPlan = (plan) => {
        navigate(`/gui/plan?year=${plan.year}&month=${plan.month}`);
    };

    return (
        <>
            <Row className="mb-3 mt-2 align-items-center">
                <Col>
                    <h4>Plany</h4>
                </Col>
                <Col className="text-end">
                    <Button size="sm" onClick={loadPlans} disabled={loading}>
                        {loading ? "Odświeżanie…" : "Odśwież"}
                    </Button>
                </Col>
            </Row>

            {error && <Alert variant="danger">{error}</Alert>}

            <Table striped hover size="sm">
                <thead>
                <tr>
                    <th>Plan</th>
                    <th>Okres</th>
                    <th className="text-end">Zaplanowane</th>
                    <th className="text-end">Spoza planu</th>
                    <th className="text-end">Opłacone</th>
                    <th className="text-end">Pozostałe</th>
                    <th className="text-end">Różnica</th>
                    <th className="text-end">Liczba pozycji planowanych</th>
                    <th className="text-end">Liczba pozycji spoza planu</th>
                    <th/>
                </tr>
                </thead>
                <tbody>
                {plans.length === 0 ? (
                    <tr>
                        <td colSpan="10" className="text-center text-muted py-4">Brak planów</td>
                    </tr>
                ) : plans.map(plan => (
                    <tr key={plan.id}>
                        <td>{plan.id}</td>
                        <td>{getMonthName(plan.month, "long")} {plan.year}</td>
                        <td className="text-end">{formatNumber(plan.plannedExpenseAmount)}</td>
                        <td className="text-end">{formatNumber(plan.unplannedExpenseAmount)}</td>
                        <td className="text-end">{formatNumber(plan.paidPlannedAmount)}</td>
                        <td className="text-end">{formatNumber(plan.remainingPlannedAmount)}</td>
                        <td className="text-end">{formatNumber(plan.plannedExpenseAmount - plan.unplannedExpenseAmount)}</td>
                        <td className="text-end">{plan.plannedCount}</td>
                        <td className="text-end">{plan.unplannedCount}</td>
                        <td className="text-end">
                            <Button size="sm" variant="outline-primary" onClick={() => openPlan(plan)}>
                                Otwórz plan
                            </Button>
                        </td>
                    </tr>
                ))}
                </tbody>
                {plans.length > 0 && (
                    <tfoot>
                    <tr>
                        <th colSpan="2" className="text-end">Razem</th>
                        <th className="text-end">{formatNumber(totals.planned)}</th>
                        <th className="text-end">{formatNumber(totals.unplanned)}</th>
                        <th className="text-end">{formatNumber(totals.paid)}</th>
                        <th className="text-end">{formatNumber(totals.remaining)}</th>
                        <th className="text-end">{formatNumber(totals.planned - totals.unplanned)}</th>
                        <th colSpan="3"/>
                    </tr>
                    </tfoot>
                )}
            </Table>
        </>
    );
};


