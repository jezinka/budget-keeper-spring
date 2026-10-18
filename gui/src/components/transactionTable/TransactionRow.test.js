import React from "react";
import {render} from "@testing-library/react";
import TransactionRow from "./TransactionRow";

const row = transferSettled => render(<table><tbody><TransactionRow transaction={{
    id: 1, transactionDate: "2026-07-18", description: "Revolut**0059* Dublin", amount: -70, transferSettled
}}/></tbody></table>).container;

describe("TransactionRow", () => {
    it("marks a transfer found in the account statement", () => {
        expect(row(true).querySelector('[title="Rozliczone w wyciągu konta"]')).not.toBeNull();
    });

    it("warns when the transfer is missing from the statement", () => {
        expect(row(false).querySelector('[title="Brak w wyciągu - zaimportuj wyciąg"]')).not.toBeNull();
    });

    it("shows nothing for a regular expense", () => {
        expect(row(undefined).querySelector('span[title]')).toBeNull();
    });
});
