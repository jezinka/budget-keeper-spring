import {useState} from "react";
import {EMPTY_OPTION, handleError, UNKNOWN_CATEGORY} from "../Utils";

export function useTransactionForm(initialState) {
    const [formState, setFormState] = useState(initialState);

    const handleChange = (event) => {
        setFormState({...formState, [event.target.name]: event.target.value});
    };

    async function loadExpense(id) {
        setFormState(initialState);
        const transaction = await fetch('/budget/expenses/' + id, {method: 'GET'});
        if (transaction.ok) {
            const data = await transaction.json();
            if (data) {
                setFormState({
                    ...formState,
                    id: id,
                    transactionDate: data.transactionDate,
                    title: data.title,
                    payee: data.payee,
                    note: data.note,
                    amount: data.amount,
                    baseSplitAmount: data.amount,
                    splitAmount: 0,
                    splitCategoryId: UNKNOWN_CATEGORY,
                    categoryId: data.categoryId ? data.categoryId : UNKNOWN_CATEGORY,
                    sourceAccountId: data.sourceAccountId ? data.sourceAccountId : EMPTY_OPTION,
                    destinationAccountId: data.destinationAccountId ? data.destinationAccountId : EMPTY_OPTION,
                    beneficiaryId: data.beneficiaryId ? data.beneficiaryId: EMPTY_OPTION,
                    manually: data.manually
                });
            }
        } else {
            handleError();
        }
    }

    return {formState, setFormState, handleChange, loadExpense};
}

export function getCategoriesMap(categories) {
    let categoriesOptions = [];

    categories.forEach(category => {
        categoriesOptions.push(
            <option key={category.id} value={category.id}>{category.name}</option>
        );
    });

    return categoriesOptions;
}

export function getAccountsMap(accounts) {
    let accountsOptions = [];
    accountsOptions.push(<option key={EMPTY_OPTION} value={EMPTY_OPTION}></option>);

    accounts.forEach(account => {
        accountsOptions.push(
            <option key={account.id} value={account.id}>{account.name}</option>
        );
    });

    return accountsOptions;
}

export function getBeneficiariesMap(beneficiaries) {
    let beneficiariesOptions = [];
    beneficiariesOptions.push(<option key={EMPTY_OPTION} value={EMPTY_OPTION}></option>);

    beneficiaries.forEach(beneficiary => {
        beneficiariesOptions.push(
            <option key={beneficiary.id} value={beneficiary.id}>{beneficiary.name}</option>
        );
    });

    return beneficiariesOptions;
}