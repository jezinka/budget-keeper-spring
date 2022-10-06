import {useEffect, useState} from "react";
import {Container, Row} from "react-bootstrap";
import LiabilityCard from "./LiabilityCard";

export default function LiabilitiesGrid() {

    const [liabilities, setLiabilities] = useState([]);

    async function reloadTable() {
        const response = await fetch('/liabilities/');
        const data = await response.json();
        setLiabilities(data);
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