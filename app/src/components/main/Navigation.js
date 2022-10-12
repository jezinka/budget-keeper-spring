import {useLocation} from "react-router-dom";
import {Container, Nav} from "react-bootstrap";
import React from "react";

const Navigation = () => {
    return (
        <Nav variant="pills" activeKey={useLocation().pathname}
             className="flex-column">
            <Container fluid className="mt-2">
                <Nav.Link href="/">Miesiąc</Nav.Link>
                <Nav.Link href="/yearAtTheGlance">Cały rok</Nav.Link>
                <Nav.Link href="/liabilities">Pasywa</Nav.Link>
                <hr/>
                <Nav.Link href="/withoutCategory">Bez kategorii</Nav.Link>
            </Container>
        </Nav>
    )
}

export default Navigation;