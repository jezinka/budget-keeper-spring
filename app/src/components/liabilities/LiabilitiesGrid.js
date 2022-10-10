import {useEffect, useState} from "react";
import {Container, Row} from "react-bootstrap";
import LiabilityCard from "./LiabilityCard";
import {handleError} from "../../Utils";

export default function LiabilitiesGrid() {

    const [liabilities, setLiabilities] = useState([]);

    async function reloadTable() {
        const response = await fetch('/liabilities/');
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

    return (
        <Container>
            <Row lg={4}>
                {liabilities.map(liability =>
                    <LiabilityCard liability={liability} reloadHandler={reloadTable}/>
                )}
            </Row>
        </Container>
    );
}