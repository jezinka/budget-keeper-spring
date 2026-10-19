import React from "react";
import {fireEvent, render, screen, waitFor} from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import {MemoryRouter} from "react-router-dom";
import PlanManager from "./PlanManager";

vi.mock("../main/Main", () => ({default: ({body}) => body}));
vi.mock("./PlanPieChart", () => ({default: () => null}));

const summary = {
    plan: {id: 7},
    plannedExpenses: [],
    plannedExpenseTransactions: [],
    unplannedExpenses: [],
    categories: [],
    plannedVsUnplanned: [],
    remainingPlannedAmount: 0,
    remainingPlannedCount: 0,
    paidPlannedAmount: 0,
    paidPlannedCount: 0
};

const response = body => ({ok: true, json: async () => body});

describe("PlanManager", () => {
    beforeEach(() => {
        global.fetch = vi.fn((url) => {
            if (url.startsWith("/budget/plans/summary")) return Promise.resolve(response(summary));
            if (url === "/budget/recurringPlannedExpenses") return Promise.resolve(response([]));
            return Promise.resolve(response({}));
        });
    });

    afterEach(() => {
        vi.restoreAllMocks();
    });

    it("creates a planned expense from the form", async () => {
        const user = userEvent.setup();
        render(<MemoryRouter future={{v7_startTransition: true, v7_relativeSplatPath: true}}><PlanManager/></MemoryRouter>);

        await user.type(await screen.findByPlaceholderText("Kwota"), "12.50");
        await user.type(screen.getByPlaceholderText("Nazwa"), "Internet");
        fireEvent.change(screen.getByLabelText("Termin planowanego wydatku"), {target: {value: "2026-10-15"}});
        await user.click(screen.getByRole("button", {name: "Dodaj do planu"}));

        await waitFor(() => expect(fetch).toHaveBeenCalledWith("/budget/plannedExpenses", expect.any(Object)));
        const [, request] = fetch.mock.calls.find(([url]) => url === "/budget/plannedExpenses");
        expect(request.method).toBe("POST");
        expect(request.headers).toEqual({"Content-Type": "application/json"});
        expect(JSON.parse(request.body)).toEqual({planId: 7, amount: 12.5, name: "Internet", dueDate: "2026-10-15"});
    });

    it("marks a planned expense as an envelope, not a single transfer", async () => {
        summary.plannedExpenses = [{id: 3, planId: 7, name: "Jedzenie na mieście", amount: 400, paid: false, singlePayment: true, spent: 0, difference: 400}];
        render(<MemoryRouter future={{v7_startTransition: true, v7_relativeSplatPath: true}}><PlanManager/></MemoryRouter>);

        fireEvent.click(await screen.findByLabelText("Jeden przelew"));

        await waitFor(() => expect(fetch).toHaveBeenCalledWith("/budget/plannedExpenses/3", expect.any(Object)));
        const [, request] = fetch.mock.calls.find(([url]) => url === "/budget/plannedExpenses/3");
        expect(request.method).toBe("PUT");
        expect(JSON.parse(request.body)).toEqual({id: 3, planId: 7, amount: 400, singlePayment: false});
        summary.plannedExpenses = [];
    });
});
