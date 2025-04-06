import React, {useContext, useState} from "react";
import {Button, Modal} from "react-bootstrap";
import AddCategoryModal from "../currentMonth/AddCategoryModal";
import {getCategoriesMap, useTransactionForm} from "../../hooks/transactionHooks";
import {EMPTY_OPTION, handleError} from "../../Utils";
import TransactionForm from "./TransactionForm";
import {CategoryContext} from "../../context/CategoryContext";

export default function EditTransactionModal(props) {
    const [showCategoryForm, setShowCategoryForm] = useState(false);
    const {categories, fetchCategories} = useContext(CategoryContext);
    const {formState, handleChange, loadExpense} = useTransactionForm({
        id: 0,
        transactionDate: Date.now(),
        title: "",
        payee: "",
        baseSplitAmount: 0,
        amount: 0,
        categoryId: EMPTY_OPTION
    });

    async function submitForm() {
        const response = await fetch('/budget/expenses/' + formState.id, {
            method: 'PUT',
            body: JSON.stringify(formState),
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
                        splitFlow={false}
                        getCategoriesMap={() => getCategoriesMap(categories)}
                        setShowCategoryForm={setShowCategoryForm}
                    />
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={props.closeHandler}> Zamknij </Button>
                    <Button variant="primary" onClick={submitForm}> Zapisz </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}