import React from "react";
import {render, screen} from "@testing-library/react";
import AccountBalances from "./AccountBalances";

describe("AccountBalances", () => {
    it("shows only non-zero balances under the account name and flags mismatching ones", async () => {
        global.fetch = vi.fn(() => Promise.resolve({
            ok: true, json: async () => [
                {accountId: 4, accountName: "Revolut", currency: "PLN", product: "Bieżące", balance: 11.12, computed: 11.12, matches: true},
                {accountId: 4, accountName: "Revolut", currency: "EUR", product: "Bieżące", balance: 0, computed: 0, matches: true},
                {accountId: 4, accountName: "Revolut", currency: "USD", product: "Inwestycje", balance: 5.77, computed: 3.94, matches: false},
            ]
        }));

        render(<AccountBalances/>);

        expect(await screen.findByText("Revolut")).toBeTruthy();
        expect(screen.getByText("11,12")).toBeTruthy();
        expect(screen.getByText("PLN")).toBeTruthy();
        expect(screen.queryByText("EUR")).toBeNull();
        expect(screen.getByText("USD (Inwestycje)")).toBeTruthy();
        expect(screen.getByText("⚠")).toBeTruthy();
        expect(screen.queryByText("Kontrola")).toBeNull();
    });
});
