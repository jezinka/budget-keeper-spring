import React from "react";
import {render, screen, waitFor, within} from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import AccountCashflowView from "./AccountCashflowView";

vi.mock("../main/Main", () => ({default: ({body}) => body}));

const accounts = [
    {id: 5, name: "Główne", defaultAccount: true, sinkingFund: false},
    {id: 1, name: "wakacje", defaultAccount: false, sinkingFund: true},
    {id: 4, name: "Revolut", defaultAccount: false, sinkingFund: false},
];
const cashflow = [
    {currency: "JPY", month: "2026-10", inflow: 0, outflow: -2005, net: -2005},
    {currency: "PLN", month: "2026-10", inflow: 100, outflow: -89, net: 11},
    {currency: "PLN", month: "2026-09", inflow: 50, outflow: -80, net: -30},
];
const transactions = [
    {id: 5, startedAt: "2023-08-06T11:05:26", completedAt: "2023-08-06T11:05:26", product: "Inwestycje", type: "Przelew", description: "Out", amount: -5.77, fee: 0, currency: "JPY", state: "ZAKOŃCZONO", balance: 0},
    {id: 4, startedAt: "2023-08-06T11:05:26", completedAt: "2023-08-06T11:05:26", product: "Inwestycje", type: "Przelew", description: "In", amount: 5.77, fee: 0, currency: "JPY", state: "ZAKOŃCZONO", balance: 5.77},
    {id: 3, startedAt: "2026-10-17T16:57:27", product: "Bieżące", type: "Płatność kartą", description: "GitHub", amount: -1985, fee: 20, currency: "JPY", state: "ZAKOŃCZONO", balance: 93},
    {id: 2, startedAt: "2026-10-16T17:19:45", product: "Bieżące", type: "Płatność kartą", description: "Anthropic", amount: -89, fee: 0, currency: "PLN", state: "ZAKOŃCZONO", balance: 11},
    {id: 1, startedAt: "2026-09-02T10:00:00", product: "Bieżące", type: "Płatność kartą", description: "Steam", amount: -80, fee: 0, currency: "PLN", state: "ZAKOŃCZONO", balance: 0},
];

const balances = [
    {accountId: 4, accountName: "Revolut", currency: "PLN", product: "Bieżące", balance: 11, computed: 11, matches: true},
    {accountId: 4, accountName: "Revolut", currency: "JPY", product: "Bieżące", balance: 93, computed: 93, matches: true},
    {accountId: 4, accountName: "Revolut", currency: "JPY", product: "Inwestycje", balance: 0, computed: -1.83, matches: false},
];

const response = body => ({ok: true, json: async () => body, text: async () => JSON.stringify(body)});

describe("AccountCashflowView", () => {
    beforeEach(() => {
        global.fetch = vi.fn((url) => {
            if (url === "/budget/accounts/all") return Promise.resolve(response(accounts));
            if (url === "/budget/accounts/4/cashflow") return Promise.resolve(response(cashflow));
            if (url === "/budget/accounts/4/transactions") return Promise.resolve(response(transactions));
            if (url === "/budget/accounts/balances") return Promise.resolve(response(balances));
            if (url === "/budget/accounts/4/import/revolut") return Promise.resolve(response({added: 3, updated: 1, unchanged: 280}));
            return Promise.reject(new Error("unexpected " + url));
        });
    });

    afterEach(() => {
        vi.restoreAllMocks();
    });

    it("offers only external accounts and filters transactions by clicked month", async () => {
        const user = userEvent.setup();
        render(<AccountCashflowView/>);

        expect(await screen.findByText("Steam")).toBeTruthy();
        expect(screen.getByLabelText("Konto")).toHaveProperty("value", "4");
        expect(within(screen.getByLabelText("Konto")).getAllByRole("option").map(o => o.textContent)).toEqual(["Revolut"]);
        expect(screen.getByText("Saldo: 11,00 PLN")).toBeTruthy();
        expect(screen.queryByText("GitHub")).toBeNull();

        await user.click(screen.getByText("2026-10"));

        expect(screen.getByText("Transakcje 2026-10")).toBeTruthy();
        expect(screen.getByText("Anthropic")).toBeTruthy();
        expect(screen.queryByText("Steam")).toBeNull();
    });

    it("switches to another currency with its own cashflow and balance", async () => {
        const user = userEvent.setup();
        render(<AccountCashflowView/>);
        await screen.findByText("Steam");

        expect(within(screen.getByLabelText("Waluta")).getAllByRole("option").map(o => o.textContent)).toEqual(["PLN", "JPY"]);
        await user.selectOptions(screen.getByLabelText("Waluta"), "JPY");

        expect(screen.getByText("GitHub")).toBeTruthy();
        expect(screen.queryByText("Steam")).toBeNull();
        expect(screen.getByText("Saldo (Bieżące): 93,00 JPY")).toBeTruthy();
        expect(screen.getByText(/Saldo \(Inwestycje\): 0,00 JPY/)).toBeTruthy();
        expect(screen.getByText(/⚠ suma -1,83/)).toBeTruthy();
        expect(screen.getAllByText("-2005,00").length).toBeGreaterThan(0);
        expect(screen.queryByText("2026-09")).toBeNull();
    });

    it("uploads statement to the selected account and shows import summary", async () => {
        const user = userEvent.setup();
        const {container} = render(<AccountCashflowView/>);
        await screen.findByText("Steam");

        await user.upload(container.querySelector('input[type="file"]'), new File(["x"], "statement.xlsx"));
        await user.click(screen.getByRole("button", {name: "Importuj"}));

        expect(await screen.findByText("Dodano: 3, zaktualizowano: 1, pominięto duplikaty: 280")).toBeTruthy();
        const [, request] = fetch.mock.calls.find(([url]) => url === "/budget/accounts/4/import/revolut");
        expect(request.method).toBe("POST");
        expect(request.body.get("file").name).toBe("statement.xlsx");
        await waitFor(() => expect(fetch.mock.calls.filter(([url]) => url === "/budget/accounts/4/cashflow")).toHaveLength(2));
    });
});
