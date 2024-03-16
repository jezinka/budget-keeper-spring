import {Button, Col, Spinner} from "react-bootstrap";
import {ArrowClockwise} from "react-bootstrap-icons";
import React, {useState} from "react";

const SpinnerLoadButton = ({loadData}) => {
    const [showSpinner, setShowSpinner] = useState(false);

    let onClick = async () => {
        setShowSpinner(true);
        await loadData();
        setShowSpinner(false);
    }

    return <Col sm={1}>
        <Button className={"mt-2"} size={"sm"} onClick={onClick}>{showSpinner ?
            <Spinner size={"sm"} animation="grow"/> : <ArrowClockwise/>}</Button>
    </Col>
}

export default SpinnerLoadButton;