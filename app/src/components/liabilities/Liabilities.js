import {Col} from "react-bootstrap";
import React from "react";
import Main from "../main/Main";
import LiabilitiesGrid from "./LiabilitiesGrid";

const Liabilities = () => {
    let body = <>
        <Col><LiabilitiesGrid/></Col>
    </>;

    return <Main body={body}/>
}

export default Liabilities;