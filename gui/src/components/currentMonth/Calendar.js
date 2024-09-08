import {Col, Container, Row} from "react-bootstrap";
import {useEffect, useState} from "react";
import {formatNumber, getDaysOfWeek} from "../../Utils";
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Tooltip from 'react-bootstrap/Tooltip';

export const Calendar = () => {
    const [data, setData] = useState([]);

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        const response = await fetch('/budget/expenses/dailyExpenses')
        const data = await response.json();
        setData(data);
    }

    function daysRows() {
        let rows = [];
        let row = [];
        let i = 1;
        const date = new Date();
        let firstDay = new Date(date.getFullYear(), date.getMonth(), 1).getDay();
        if (firstDay === 0) {
            firstDay = 7;
        }
        const lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();

        const emptyCol = <Col/>;
        for (let j = 0; j < firstDay - 1; j++) {
            row.push(emptyCol);
        }

        while (i <= lastDay) {
            let className = "text-sm-center";
            let day = data.find(d => d.day === i);
            let tooltipText = null;
            if (day !== undefined) {
                if (i !== date.getDate()) {
                    className += " bg-info";
                }
                tooltipText = formatNumber(day.amount);
            }

            if (i === date.getDate()) {
                className += " fw-bolder bg-success";
            }

            if (i > date.getDate()) {
                className += " bg-dark-subtle text-muted";
            }

            row.push(<OverlayTrigger
                placement={'bottom'}
                overlay={<Tooltip id={`tooltip-cell-${i}`}>
                    {tooltipText}
                </Tooltip>}>
                <Col className={className}>{i}</Col>
            </OverlayTrigger>);

            if (row.length === 7) {
                rows.push(<Row>{row}</Row>);
                row = [];
            }
            i++;
        }
        if (row.length > 0) {
            rows.push(<Row>{row}</Row>);
            while (row.length < 7) {
                row.push(emptyCol);
            }
        }
        return rows;
    }

    return (
        <Container>
            <Col sm={7} className={"ms-5"}>
                <Row>{getDaysOfWeek().map(d => <Col className="text-sm-center">{d}</Col>)}</Row>
                {daysRows()}
            </Col>
        </Container>)
}