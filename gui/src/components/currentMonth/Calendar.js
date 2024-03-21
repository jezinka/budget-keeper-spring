import {Col, Container, Row} from "react-bootstrap";
import {useEffect, useState} from "react";
import {getDaysOfWeek} from "../../Utils";

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
        const firstDay = new Date(date.getFullYear(), date.getMonth(), 1).getDay();
        const lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();

        const emptyCol = <Col/>;
        for (let j = 0; j < firstDay - 1; j++) {
            row.push(emptyCol);
        }

        while (i <= lastDay) {
            let className = "text-sm-center";
            if (data.filter(d => d.day === i).length > 0)
                className += " bg-info";

            if (i === date.getDate()) {
                className += " fw-bolder bg-success";
            }

            if (i > date.getDate()) {
                className += " bg-dark-subtle text-muted";
            }

            row.push(<Col className={className}>{i}</Col>);

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
            <Col sm={7}>
                <Row>{getDaysOfWeek().map(d => <Col className="text-sm-center" key={d}>{d}</Col>)}</Row>
                {daysRows()}
            </Col>
        </Container>
    )
}