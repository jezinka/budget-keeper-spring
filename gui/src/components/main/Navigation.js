import {useLocation} from "react-router-dom";
import {Container, Nav, NavDropdown} from "react-bootstrap";
import React from "react";

const Navigation = () => {
    return (
        <Nav variant="pills" activeKey={useLocation().pathname}
             className="flex-column">
            <Container fluid className="mt-2">
                <Nav.Link href="/">Miesiąc</Nav.Link>
                <NavDropdown title="Cały rok" id="basic-nav-dropdown" drop={"end"}>
                    <NavDropdown.Item href="/gui/yearAtTheGlance">Tabelka</NavDropdown.Item>
                    <NavDropdown.Item href="/gui/yearAtTheGlanceBarChart">Bar Chart</NavDropdown.Item>
                    <NavDropdown.Item href="/gui/yearAtTheGlancePieChart">Pie Chart</NavDropdown.Item>
                </NavDropdown>
                <Nav.Link href="/gui/fixedCosts">Koszty stałe</Nav.Link>
                <hr/>
                <Nav.Link href="/gui/allTransactions">Wszystkie transakcje</Nav.Link>
                <Nav.Link href="/gui/logs">Logi</Nav.Link>
            </Container>
        </Nav>
    )
}

export default Navigation;