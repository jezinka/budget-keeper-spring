import {Col, Form, Row} from "react-bootstrap";
import React from "react";
import {FIRST_YEAR, getMonthName} from "../../Utils";

const MonthYearFilter = ({year, month, onYearChange, onMonthChange}) => {

    const handleYearChange = (event) => {
        onYearChange(parseInt(event.target.value));
    };

    const handleMonthChange = (event) => {
        onMonthChange(parseInt(event.target.value));
    };

    let currentYear = new Date().getFullYear();
    const yearList = Array.from({length: currentYear - FIRST_YEAR + 1}, (_, i) => currentYear - i);
    const months = Array.from({length: 12}, (_, i) => i + 1);

    return (
        <Row className="mb-3">
            <Col sm={2}>
                <Form.Group>
                    <Form.Label>Rok:</Form.Label>
                    <Form.Select size="sm" onChange={handleYearChange} value={year}>
                        {yearList.map((y) => <option key={y} value={y}>{y}</option>)}
                    </Form.Select>
                </Form.Group>
            </Col>
            <Col sm={2}>
                <Form.Group>
                    <Form.Label>Miesiąc:</Form.Label>
                    <Form.Select size="sm" onChange={handleMonthChange} value={month}>
                        {months.map((m) => (
                            <option key={m} value={m}>{getMonthName(m, 'long')}</option>
                        ))}
                    </Form.Select>
                </Form.Group>
            </Col>
        </Row>
    );
}

export default MonthYearFilter;
