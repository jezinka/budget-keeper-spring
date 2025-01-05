import {useLocation} from "react-router-dom";
import {Container, Nav} from "react-bootstrap";
import React from "react";

const Navigation = () => {
    return (
        <Nav variant="pills" activeKey={useLocation().pathname}
             className="flex-column">
            <Container fluid className="mt-2">
                <Nav.Link href="/budget">Miesiąc</Nav.Link>
                <Nav.Link href="/budget/gui/yearAtTheGlance">Cały rok</Nav.Link>
                <Nav.Link href="/budget/gui/plan">Plan</Nav.Link>
                <Nav.Link href="/budget/gui/lifestyleInflation">Inflacja Życia</Nav.Link>
                <hr/>
                <Nav.Link href="/budget/gui/allTransactions">Wszystkie transakcje</Nav.Link>
                <Nav.Link href="/budget/gui/logs">Logi</Nav.Link>
            </Container>
        </Nav>
    )
}

export default Navigation;