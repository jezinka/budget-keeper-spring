import React, {useEffect, useState} from "react";
import {ButtonGroup, Card, Col, Form, Row, Table, ToggleButton} from "react-bootstrap";
import {formatNumber} from "../../Utils";
import Main from "../main/Main";
import TrendTable from "./TrendTable";
import TrendSparkline from "./TrendSparkline";
import {changeColor, formatDiff, formatPercent, groupTrendsByLevel} from "./trendUtils";

const TOP_COUNT = 5;

const TopList = ({title, trends}) => (
    <Card className="h-100">
        <Card.Header><strong>{title}</strong></Card.Header>
        <Card.Body className="p-2">
            {trends.length === 0
                ? <span className="text-muted">Brak</span>
                : (
                    <Table size="sm" borderless className="mb-0">
                        <tbody>
                        {trends.map(t => (
                            <tr key={t.category}>
                                <td className="align-middle">{t.category}</td>
                                <td><TrendSparkline trend={t}/></td>
                                <td className="align-middle"
                                    style={{textAlign: "right", color: changeColor(t.diffPercent), fontWeight: "bold"}}>
                                    {formatDiff(t.diff)} zł
                                </td>
                                <td className="align-middle"
                                    style={{textAlign: "right", color: changeColor(t.diffPercent)}}>
                                    {formatPercent(t.diffPercent)}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </Table>
                )}
        </Card.Body>
    </Card>
);

const LivingExpensesView = () => {
    const [trends, setTrends] = useState([]);
    const [levels, setLevels] = useState([]);
    const [groupBy, setGroupBy] = useState("category");
    const [minAmount, setMinAmount] = useState(500);

    useEffect(() => {
        fetch("/budget/expenses/categoryTrends")
            .then(r => r.json())
            .then(setTrends);
        fetch("/budget/categories/levels")
            .then(r => r.json())
            .then(setLevels);
    }, []);

    const rows = (groupBy === "level" ? groupTrendsByLevel(trends, levels) : trends)
        .filter(t => Math.max(t.last12, t.previous12) >= (Number(minAmount) || 0));

    const risers = rows.filter(t => t.diff > 0).sort((a, b) => b.diff - a.diff).slice(0, TOP_COUNT);
    const fallers = rows.filter(t => t.diff < 0).sort((a, b) => a.diff - b.diff).slice(0, TOP_COUNT);

    const totalLast12 = trends.reduce((s, t) => s + t.last12, 0);
    const totalPrevious12 = trends.reduce((s, t) => s + t.previous12, 0);
    const totalPercent = totalPrevious12 === 0 ? null : (totalLast12 - totalPrevious12) / totalPrevious12;

    const body = (
        <Col sm={12}>
            <h2>Na co wydaję coraz więcej</h2>

            {trends.length > 0 && (
                <>
                    <p className="lead mb-3">
                        Ostatnie 12 mies.: <strong>{formatNumber(totalLast12)} zł</strong>{" "}
                        <span style={{color: changeColor(totalPercent)}}>
                            ({formatDiff(totalLast12 - totalPrevious12)} zł, {formatPercent(totalPercent)} r/r)
                        </span>
                    </p>

                    <Row className="mb-3 align-items-center g-2">
                        <Col xs="auto">
                            <ButtonGroup size="sm">
                                {[["category", "Kategorie"], ["level", "Poziomy kategorii"]].map(([value, label]) => (
                                    <ToggleButton key={value} id={"group-" + value} type="radio"
                                                  variant="outline-primary" value={value}
                                                  checked={groupBy === value}
                                                  onChange={e => setGroupBy(e.currentTarget.value)}>
                                        {label}
                                    </ToggleButton>
                                ))}
                            </ButtonGroup>
                        </Col>
                        <Col xs="auto">
                            <Form.Label className="mb-0 me-2" htmlFor="minAmount">Ukryj poniżej (zł / 12 mies.)</Form.Label>
                        </Col>
                        <Col xs="auto">
                            <Form.Control id="minAmount" size="sm" type="number" step={100} min={0}
                                          style={{width: 110}} value={minAmount}
                                          onChange={e => setMinAmount(e.target.value)}/>
                        </Col>
                    </Row>

                    <Row className="mb-4 g-3">
                        <Col md={6}><TopList title="▲ Rośnie najbardziej" trends={risers}/></Col>
                        <Col md={6}><TopList title="▼ Spada najbardziej" trends={fallers}/></Col>
                    </Row>

                    <h5>Wszystkie {groupBy === "level" ? "poziomy" : "kategorie"}</h5>
                    <p className="text-muted small mb-2">
                        Ostatnie 12 pełnych miesięcy vs 12 miesięcy wcześniej. Trend: sumy z pełnych lat
                        i ostatnie 12 mies. jako ostatni punkt. Kliknij wiersz, żeby zobaczyć miesiące rok do roku.
                    </p>
                    <TrendTable key={groupBy} trends={rows}/>
                </>
            )}
        </Col>
    );

    return <Main body={body}/>;
};

export default LivingExpensesView;
