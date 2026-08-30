import React, {useContext, useEffect, useState} from "react";
import {Button, Modal} from "react-bootstrap";
import AddCategoryModal from "../currentMonth/AddCategoryModal";
import {getAccountsMap, getBeneficiariesMap, getCategoriesMap, useTransactionForm} from "../../hooks/transactionHooks";
import {EMPTY_OPTION, handleError, UNKNOWN_CATEGORY} from "../../Utils";
import TransactionForm from "./TransactionForm";
import {CategoryContext} from "../../context/CategoryContext";

export default function SplitTransactionModal(props) {
    const [showCategoryForm, setShowCategoryForm] = useState(false);
    const {categories, fetchCategories} = useContext(CategoryContext);
    const [accounts, setAccounts] = useState([]);
    const [beneficiaries, setBeneficiaries] = useState([]);

    const {formState, setFormState, handleChange, loadExpense} = useTransactionForm({
        id: 0,
        transactionDate: Date.now(),
        title: "",
        payee: "",
        baseSplitAmount: 0,
        amount: 0,
        categoryId: UNKNOWN_CATEGORY,
        splitAmount: 0,
        splitCategoryId: UNKNOWN_CATEGORY,
        sourceAccountId: EMPTY_OPTION,
        destinationAccountId: EMPTY_OPTION,
        beneficiaryId: EMPTY_OPTION
    });

    useEffect(() => {
        fetchAccounts();
        fetchBeneficiaries();
    }, []);

    const handleSplit = (event) => {
        let value = Number(event.target.value);
        let newValue = Number((formState.baseSplitAmount - value).toFixed(2));
        setFormState({...formState, [event.target.name]: value, amount: newValue});
    };

    async function fetchAccounts() {
        const response = await fetch('/budget/accounts/all');
        if (response.ok) {
            const data = await response.json();
            if (data) {
                setAccounts(data);
            }
        } else {
            handleError();
        }
    }
    async function fetchBeneficiaries() {
        const response = await fetch('/budget/beneficiaries/all');
        if (response.ok) {
            const data = await response.json();
            if (data) {
                setBeneficiaries(data);
            }
        } else {
            handleError();
        }
    }

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
                        getAccountsMap={() => getAccountsMap(accounts)}
                        getBeneficiariesMap={() => getBeneficiariesMap(beneficiaries)}
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