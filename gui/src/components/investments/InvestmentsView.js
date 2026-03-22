import React, {useEffect, useState} from 'react';
import Main from '../main/Main';
import PortfolioChart from './PortfolioChart';
import FireProgressBar from './FireProgressBar';
import {Col, Row} from 'react-bootstrap';

const InvestmentsView = () => {
    const [fireStages, setFireStages] = useState([]);
    const [currentValue, setCurrentValue] = useState(null);

    useEffect(() => {
        fetch('/budget/portfolio/fire-stages')
            .then(res => res.json())
            .then(setFireStages)
            .catch(() => {});

        fetch('/budget/portfolio/snapshots')
            .then(res => res.json())
            .then(data => {
                if (data.length) setCurrentValue(data[data.length - 1].value);
            })
            .catch(() => {});
    }, []);

    return (
        <Main body={
            <Col>
                <Row className="mt-3">
                    <Col>
                        <h5>Wartość portfela w czasie</h5>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <PortfolioChart fireStages={fireStages}/>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <FireProgressBar fireStages={fireStages} currentValue={currentValue}/>
                    </Col>
                </Row>
            </Col>
        }/>
    );
};

export default InvestmentsView;
