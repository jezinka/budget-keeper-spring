import {Col, Form} from "react-bootstrap";
import React from "react";

const YearFilter = ({formState, formHandler}) => {

    const handleChange = (event) => {
        formHandler({...formState, [event.target.name]: event.target.value});
    };

    return <Col sm={1}>
        <Form>
            <Form.Select className="m-2" size="sm" placeholder="Rok:" onChange={handleChange}
                         name="year" value={formState.year}>
                {[2022, 2021].map((year) => <option key={year} value={year}>{year}</option>)}
            </Form.Select>
        </Form>
    </Col>;
}

export default YearFilter;