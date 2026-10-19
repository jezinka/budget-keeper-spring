import React from "react";
import {fireEvent, render, screen, waitFor} from "@testing-library/react";
import Dashboard from "./Dashboard";

vi.mock("../main/Main", () => ({default: ({body}) => <div>{body}</div>}));

const summary = {
    plan: {id: 7, year: 2026, month: 10},
    plannedExpenses: [
        {id: 1, name: "Czynsz", amount: 2000, paid: false, dueDate: "2000-01-10"},
        {id: 2, name: "Prąd", amount: 150, paid: true, dueDate: "2000-01-05"},
        {id: 3, name: "Internet", amount: 60, paid: false, dueDate: null},
    ],
    categories: [
        {categoryName: "Jedzenie", plannedAmount: 1000, underPlanned: true, difference: -120.5},
        {categoryName: "Hobby", plannedAmount: 0, underPlanned: true, difference: -50},
        {categoryName: "Transport", plannedAmount: 300, underPlanned: false, difference: 100},
    ],
    remainingPlannedAmount: 2060,
    remainingPlannedCount: 2,
    unplannedExpenseAmount: 50,
};

const responses = {
    "/budget/plans/summary": summary,
    "/budget/moneyAmount": {accountBalance: 5000, expenses: -3000},
    "/budget/expenses/currentMonth": [
        {id: 10, categoryId: -999},
        {id: 11, categoryId: 4, transferSettled: false},
        {id: 12, categoryId: 4},
    ],
};

describe("Dashboard", () => {
    beforeEach(() => {
        global.fetch = vi.fn((url) => Promise.resolve({
            ok: true,
            json: async () => responses[url.split("?")[0]] ?? {},
        }));
    });

    it("lists action points, unpaid planned expenses and marks one as paid", async () => {
        render(<Dashboard/>);

        expect(await screen.findByText("Transakcje bez kategorii: 1")).toBeTruthy();
        expect(screen.getByText("Nierozliczone przelewy: 1")).toBeTruthy();
        expect(screen.getByText("Przekroczony plan: Jedzenie o 120,50")).toBeTruthy();
        expect(screen.queryByText(/Hobby/)).toBeNull();
        expect(screen.queryByText(/Transport/)).toBeNull();
        expect(screen.queryByText("Brak planu na ten miesiąc")).toBeNull();

        expect(screen.getByText("Czynsz")).toBeTruthy();
        expect(screen.getByText("Internet")).toBeTruthy();
        expect(screen.queryByText("Prąd")).toBeNull();
        expect(screen.getByText("2000-01-10").className).toContain("bg-danger");
        expect(screen.getByText("2940,00")).toBeTruthy(); // wolne środki: 5000 - 2060

        fireEvent.click(screen.getAllByText("Zapłacone")[0]);
        await waitFor(() => expect(fetch).toHaveBeenCalledWith("/budget/plannedExpenses/1/paid?paid=true", {method: "PATCH"}));
    });

    it("flags a missing plan", async () => {
        responses["/budget/plans/summary"] = {...summary, plan: null, plannedExpenses: [], categories: []};
        render(<Dashboard/>);
        expect(await screen.findByText("Brak planu na ten miesiąc")).toBeTruthy();
        expect(screen.getByText("Nic nie zostało do zapłacenia")).toBeTruthy();
    });
});
