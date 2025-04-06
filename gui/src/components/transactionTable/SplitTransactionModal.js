import React, {useContext, useState} from "react";
import {Button, Modal} from "react-bootstrap";
import AddCategoryModal from "../currentMonth/AddCategoryModal";
import {getCategoriesMap, useTransactionForm} from "../../hooks/transactionHooks";
import {EMPTY_OPTION, handleError} from "../../Utils";
import TransactionForm from "./TransactionForm";
import {CategoryContext} from "../../context/CategoryContext";

export default function SplitTransactionModal(props) {
    const [showCategoryForm, setShowCategoryForm] = useState(false);
    const {categories, fetchCategories} = useContext(CategoryContext);
    const {formState, setFormState, handleChange, loadExpense} = useTransactionForm({
        id: 0,
        transactionDate: Date.now(),
        title: "",
        payee: "",
        baseSplitAmount: 0,
        amount: 0,
        categoryId: EMPTY_OPTION,
        splitAmount: 0,
        splitCategoryId: EMPTY_OPTION
    });

    const handleSplit = (event) => {
        let value = Number(event.target.value);
        let newValue = Number((formState.baseSplitAmount - value).toFixed(2));
        setFormState({...formState, [event.target.name]: value, amount: newValue});
    };

    async function submitForm() {
        const splittedTransactions = [
            {
                ...formState,
                id: null,
                amount: formState.splitAmount,
                categoryId: formState.splitCategoryId
            },
            {
                ...formState,
                id: null,
                categoryId: formState.categoryId
            }
        ];

        const response = await fetch('/budget/expenses/split/' + formState.id, {
            method: 'POST',
            body: JSON.stringify(splittedTransactions),
            headers: {'Content-Type': 'application/json'},
        });

        props.closeHandler();
        if (response.ok) {
            props.changeTransactionsHandler();
        } else {
            handleError();
        }
    }

    return (
        <>
            <AddCategoryModal show={showCategoryForm} close={() => {
                setShowCategoryForm(false);
                fetchCategories();
            }}/>

            <Modal size="lg" show={props.show} onHide={props.closeHandler} onShow={() => loadExpense(props.id)}>
                <Modal.Header closeButton>
                    <Modal.Title>Edytuj transakcję:</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <TransactionForm
                        formState={formState}
                        handleChange={handleChange}
                        handleSplit={handleSplit}
                        splitFlow={true}
                        getCategoriesMap={() => getCategoriesMap(categories)}
                        setShowCategoryForm={setShowCategoryForm}
                    />
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={props.closeHandler}> Zamknij </Button>
                    <Button variant="primary" onClick={submitForm}> Rozdziel </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}