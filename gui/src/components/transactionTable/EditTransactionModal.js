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
    const {formState, setFormState, handleChange, loadExpense} = useTransactionForm({
        id: null,
        transactionDate: new Date().toISOString().slice(0,10),
        title: "",
        payee: "",
        note: null,
        baseSplitAmount: 0,
        amount: 0,
        categoryId: EMPTY_OPTION,
        manually: false
    });

    async function submitForm() {
        // jeśli istnieje id -> edycja, inaczej tworzenie
        if (formState.id) {
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
        } else {
            // create
            const payload = {
                amount: formState.baseSplitAmount,
                transactionDate: formState.transactionDate,
                title: formState.title,
                payee: formState.payee,
                note: formState.note,
                manually: formState.manually,
                categoryId: formState.categoryId === EMPTY_OPTION ? null : (formState.categoryId === -1 ? -1 : formState.categoryId)
            };

            const response = await fetch('/budget/expenses/create', {
                method: 'POST',
                body: JSON.stringify(payload),
                headers: {'Content-Type': 'application/json'},
            });

            props.closeHandler();
            if (response.ok) {
                props.changeTransactionsHandler();
            } else {
                handleError();
            }
        }
    }

    function prepareForCreate() {
        setFormState({
            id: null,
            transactionDate: new Date().toISOString().slice(0,10),
            title: "",
            payee: "",
            note: null,
            baseSplitAmount: 0,
            amount: 0,
            categoryId: EMPTY_OPTION,
            manually: true
        });
    }

    // onShow: jeśli props.id jest ustawione -> załaduj expense, inaczej przygotuj pusty formularz
    const onShowHandler = () => {
        if (props.id) {
            loadExpense(props.id);
        } else {
            prepareForCreate();
        }
    };

    return (
        <>
            <AddCategoryModal show={showCategoryForm} close={() => {
                setShowCategoryForm(false);
                fetchCategories();
            }}/>

            <Modal size="lg" show={props.show} onHide={props.closeHandler} onShow={onShowHandler}>
                <Modal.Header closeButton>
                    <Modal.Title>{formState && formState.id ? 'Edytuj transakcję:' : 'Dodaj transakcję:'}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <TransactionForm
                        formState={formState}
                        handleChange={handleChange}
                        splitFlow={false}
                        getCategoriesMap={() => getCategoriesMap(categories)}
                        setShowCategoryForm={setShowCategoryForm}
                        editable={formState.manually === true || formState.id === null}
                    />
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={props.closeHandler}> Zamknij </Button>
                    <Button variant="primary" onClick={submitForm}> {formState && formState.id ? 'Zapisz' : 'Dodaj'} </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}