import {useState} from "react";
import {EMPTY_OPTION, handleError} from "../Utils";

export function useTransactionForm(initialState) {
    const [formState, setFormState] = useState(initialState);

    const handleChange = (event) => {
        setFormState({...formState, [event.target.name]: event.target.value});
    };

    async function loadExpense(id) {
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
                    categoryId: data.categoryId ? data.categoryId : EMPTY_OPTION
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
    categoriesOptions.push(<option key={EMPTY_OPTION} value={EMPTY_OPTION}></option>);

    categories.forEach(category => {
        categoriesOptions.push(
            <option key={category.id} value={category.id}>{category.name}</option>
        );
    });

    return categoriesOptions;
}