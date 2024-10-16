import {useLocation} from "react-router-dom";
import {Container, Nav, NavDropdown} from "react-bootstrap";
import React from "react";

const Navigation = () => {
    return (
        <Nav variant="pills" activeKey={useLocation().pathname}
             className="flex-column">
            <Container fluid className="mt-2">
                <Nav.Link href="/budget">Miesiąc</Nav.Link>
                <NavDropdown title="Cały rok" id="basic-nav-dropdown" drop={"end"}>
                    <NavDropdown.Item href="/budget/gui/yearAtTheGlance">Tabelka</NavDropdown.Item>
                    <NavDropdown.Item href="/budget/gui/yearAtTheGlanceBarChart">Bar Chart</NavDropdown.Item>
                    <NavDropdown.Item href="/budget/gui/yearAtTheGlancePieChart">Pie Chart</NavDropdown.Item>
                </NavDropdown>
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