import {useEffect, useState} from "react";
import {Container, Row} from "react-bootstrap";
import LiabilityCard from "./LiabilityCard";
import {EMPTY_OPTION, getDate, handleError} from "../../Utils";
import AddOutcomeForm from "./AddOutcomeForm";
import GraphModal from "./GraphModal";

export default function LiabilitiesGrid() {
    const [showForm, setShowForm] = useState(false);
    const [showGraph, setShowGraph] = useState(false);
    const [formState, setFormState] = useState({
        "date": getDate(), "liability": EMPTY_OPTION, "outcome": 0
    })
    const [liabilities, setLiabilities] = useState([]);

    async function reloadTable() {
        const response = await fetch('/liabilityLookouts/');
        if (response.ok) {
            const data = await response.json();
            if (data) {
                return setLiabilities(data)
            }
        }
        return handleError();
    }

    useEffect(() => {
        reloadTable();
    }, []);

    return (<>
            <AddOutcomeForm formState={formState} setFormState={setFormState}
                            showForm={showForm} setShowForm={setShowForm}
                            reloadHandler={reloadTable}/>
            <GraphModal showGraph={showGraph} setShowGraph={setShowGraph} liabilityId={formState.liability}/>
            <Container>
                <Row lg={4}>
                    {liabilities.map(liability =>
                        <LiabilityCard liability={liability}
                                       formState={formState}
                                       setFormState={setFormState}
                                       setShowForm={setShowForm}
                                       setShowGraph={setShowGraph}/>
                    )}
                </Row>
            </Container>
        </>
    );
}