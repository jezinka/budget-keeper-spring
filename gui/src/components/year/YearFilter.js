import {Col, Form} from "react-bootstrap";
import React from "react";
import {FIRST_YEAR} from "../../Utils";

const YearFilter = ({year, formHandler}) => {

    const handleChange = (event) => {
        formHandler(parseInt(event.target.value));
    };

    let currentYear = new Date().getFullYear();
    const yearList = Array.from({length: currentYear - FIRST_YEAR + 1}, (_, i) => currentYear - i)

    return <Col sm={1}>
        <Form>
            <Form.Select className="m-2" size="sm" placeholder="Rok:" onChange={handleChange}
                         name="year" value={year}>
                {yearList.map((year) => <option key={year} value={year}>{year}</option>)}
            </Form.Select>
        </Form>
    </Col>;
}

export default YearFilter;