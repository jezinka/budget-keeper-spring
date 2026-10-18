import React, {useCallback, useEffect, useState} from 'react';
import {Alert, Card, Col, Form, Row, Table} from "react-bootstrap";
import {Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis} from "recharts";
import Main from "../main/Main";
import PlanImport from "../plan/PlanImport";
import {formatNumber} from "../../Utils";

const COMPLETED = ['ZAKOŃCZONO', 'COMPLETED'];

const AccountCashflowView = () => {
    const [accounts, setAccounts] = useState([]);
    const [accountId, setAccountId] = useState(null);
    const [cashflow, setCashflow] = useState([]);
    const [transactions, setTransactions] = useState([]);
    const [allBalances, setAllBalances] = useState([]);
    const [month, setMonth] = useState(null);
    const [currency, setCurrency] = useState(null);
    const [importResult, setImportResult] = useState(null);

    useEffect(() => {
        fetch('/budget/accounts/all')
            .then(res => res.json())
            .then(all => {
                const external = all.filter(a => !a.defaultAccount && !a.sinkingFund);
                setAccounts(external);
                const revolut = external.find(a => a.name.toLowerCase().includes('revolut')) || external[0];
                if (revolut) setAccountId(revolut.id);
            })
            .catch(() => {
            });
    }, []);

    const load = useCallback(() => {
        if (!accountId) return;
        fetch(`/budget/accounts/${accountId}/cashflow`).then(res => res.json()).then(setCashflow);
        fetch(`/budget/accounts/${accountId}/transactions`).then(res => res.json()).then(setTransactions);
        fetch('/budget/accounts/balances').then(res => res.json()).then(setAllBalances);
    }, [accountId]);

    useEffect(load, [load]);

    const onImported = (text) => {
        const r = JSON.parse(text);
        setImportResult(`Dodano: ${r.added}, zaktualizowano: ${r.updated}, pominięto duplikaty: ${r.unchanged}`);
        load();
    };

    const currencies = [...new Set(transactions.map(t => t.currency))]
        .sort((a, b) => (b === 'PLN') - (a === 'PLN') || a.localeCompare(b));
    const selectedCurrency = currencies.includes(currency) ? currency : currencies[0];
    const currencyTransactions = transactions.filter(t => t.currency === selectedCurrency);
    const currencyCashflow = cashflow.filter(m => m.currency === selectedCurrency);

    const shownTransactions = month
        ? currencyTransactions.filter(t => t.startedAt.startsWith(month))
        : currencyTransactions.slice(0, 50);
    // Revolut keeps a separate balance per product (Bieżące, Inwestycje...)
    const balances = allBalances.filter(b => b.accountId === accountId && b.currency === selectedCurrency);

    const body = <Col>
        <Row className="mt-3 align-items-end">
            <Col sm={2}>
                <Form.Label>Konto</Form.Label>
                <Form.Select aria-label="Konto" value={accountId || ''} onChange={e => {
                    setAccountId(Number(e.target.value));
                    setMonth(null);
                }}>
                    {accounts.map(a => <option key={a.id} value={a.id}>{a.name}</option>)}
                </Form.Select>
            </Col>
            <Col sm={1}>
                <Form.Label>Waluta</Form.Label>
                <Form.Select aria-label="Waluta" value={selectedCurrency || ''} onChange={e => {
                    setCurrency(e.target.value);
                    setMonth(null);
                }}>
                    {currencies.map(c => <option key={c} value={c}>{c}</option>)}
                </Form.Select>
            </Col>
            <Col sm={4}>
                <Form.Label>Import wyciągu Revolut (.xlsx / .csv, jeden plik na walutę)</Form.Label>
                {accountId && <PlanImport uploadUrl={`/budget/accounts/${accountId}/import/revolut`}
                                          closeHandler={onImported}/>}
            </Col>
            <Col sm={3}>
                {balances.map(b => <h5 key={b.product}>
                    Saldo{balances.length > 1 ? ` (${b.product})` : ''}: {formatNumber(b.balance)} {b.currency}
                    {!b.matches && <small className="text-danger" title="Suma zaimportowanych transakcji różni się od salda z wyciągu - brakuje części historii">
                        {' '}⚠ suma {formatNumber(b.computed)}</small>}
                </h5>)}
            </Col>
        </Row>
        {importResult && <Alert variant="success" className="mt-2" dismissible
                                onClose={() => setImportResult(null)}>{importResult}</Alert>}
        <Row className="mt-3">
            <Col sm={7}>
                <ResponsiveContainer width="100%" height={300}>
                    <BarChart data={currencyCashflow.slice(0, 12).reverse()}>
                        <CartesianGrid strokeDasharray="3 3"/>
                        <XAxis dataKey="month"/>
                        <YAxis/>
                        <Tooltip formatter={v => formatNumber(v)}/>
                        <Legend/>
                        <Bar dataKey="inflow" name="Wpływy" fill="#33A02C"/>
                        <Bar dataKey="outflow" name="Wydatki" fill="#E31A1C"/>
                    </BarChart>
                </ResponsiveContainer>
            </Col>
            <Col sm={5}>
                <Card>
                    <Card.Body className="p-0" style={{maxHeight: 300, overflowY: 'auto'}}>
                        <Table size="sm" hover className="mb-0">
                            <thead>
                            <tr>
                                <th>Miesiąc</th>
                                <th className="text-end">Wpływy</th>
                                <th className="text-end">Wydatki</th>
                                <th className="text-end">Bilans</th>
                            </tr>
                            </thead>
                            <tbody>
                            {currencyCashflow.map(m => (
                                <tr key={m.month} onClick={() => setMonth(month === m.month ? null : m.month)}
                                    className={month === m.month ? 'table-active' : ''} style={{cursor: 'pointer'}}>
                                    <td>{m.month}</td>
                                    <td className="text-end">{formatNumber(m.inflow)}</td>
                                    <td className="text-end">{formatNumber(m.outflow)}</td>
                                    <td className={'text-end ' + (m.net < 0 ? 'text-danger' : '')}>{formatNumber(m.net)}</td>
                                </tr>
                            ))}
                            </tbody>
                        </Table>
                    </Card.Body>
                </Card>
            </Col>
        </Row>
        <h6 className="mt-3">{month ? `Transakcje ${month}` : 'Ostatnie transakcje'}</h6>
        <Table size="sm" striped bordered>
            <thead>
            <tr>
                <th>Data</th>
                <th>Produkt</th>
                <th>Rodzaj</th>
                <th>Opis</th>
                <th className="text-end">Kwota</th>
                <th className="text-end">Opłata</th>
                <th>Status</th>
            </tr>
            </thead>
            <tbody>
            {shownTransactions.map(t => (
                <tr key={t.id} className={COMPLETED.includes(t.state) ? '' : 'text-muted'}>
                    <td>{t.startedAt.replace('T', ' ')}</td>
                    <td>{t.product}</td>
                    <td>{t.type}</td>
                    <td>{t.description}</td>
                    <td className={'text-end ' + (t.amount < 0 ? 'text-danger' : '')}>{formatNumber(t.amount)}</td>
                    <td className="text-end">{t.fee ? formatNumber(t.fee) : ''}</td>
                    <td>{t.state}</td>
                </tr>
            ))}
            </tbody>
        </Table>
    </Col>;

    return <Main body={body}/>;
};

export default AccountCashflowView;
