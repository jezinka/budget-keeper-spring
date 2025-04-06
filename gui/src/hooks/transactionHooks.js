import { useState, useEffect } from "react";
import { EMPTY_OPTION, handleError } from "../Utils";

export function useCategories() {
    const [categories, setCategories] = useState([]);

    useEffect(() => {
        fetchCategories();
    }, []);

    async function fetchCategories() {
        const response = await fetch('/budget/categories/all');
        if (response.ok) {
            const data = await response.json();
            if (data) {
                setCategories(data);
            }
        } else {
            handleError();
        }
    }

    function getCategoriesMap() {
        let categoriesList = [<option key={EMPTY_OPTION} value={EMPTY_OPTION}></option>];
        categories.forEach((c) => {
            categoriesList.push(<option key={c.id} value={c.id}>{c.name}</option>);
        });
        return categoriesList;
    }

    return { categories, fetchCategories, getCategoriesMap };
}

export function useTransactionForm(initialState) {
    const [formState, setFormState] = useState(initialState);

    const handleChange = (event) => {
        setFormState({ ...formState, [event.target.name]: event.target.value });
    };

    async function loadExpense(id) {
        const transaction = await fetch('/budget/expenses/' + id, { method: 'GET' });
        if (transaction.ok) {
            const data = await transaction.json();
            if (data) {
                setFormState({
                    ...formState,
                    id: id,
                    transactionDate: data.transactionDate,
                    title: data.title,
                    payee: data.payee,
                    amount: data.amount,
                    baseSplitAmount: data.amount,
                    categoryId: data.categoryId ? data.categoryId : EMPTY_OPTION
                });
            }
        } else {
            handleError();
        }
    }

    return { formState, setFormState, handleChange, loadExpense };
}