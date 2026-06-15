import React from "react";
import {Tab, Tabs} from "react-bootstrap";
import Main from "../main/Main";
import {CategoryAdminContent} from "./CategoryAdmin";
import {AllTransactionsContent} from "../allTransactions/AllTransactions";
import LogTable from "../logs/LogTable";

const AdminPanel = () => {
    const body = (
        <Tabs defaultActiveKey="categories" className="mb-3">
            <Tab eventKey="categories" title="Kategorie">
                <CategoryAdminContent/>
            </Tab>
            <Tab eventKey="transactions" title="Wszystkie transakcje">
                <AllTransactionsContent/>
            </Tab>
            <Tab eventKey="logs" title="Logi">
                <LogTable/>
            </Tab>
        </Tabs>
    );

    return <Main body={body}/>;
};

export default AdminPanel;
