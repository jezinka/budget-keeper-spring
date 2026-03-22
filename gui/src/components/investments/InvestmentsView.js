import React, {useEffect, useState} from 'react';
import Main from '../main/Main';
import PortfolioChart from './PortfolioChart';
import FireProgressBar from './FireProgressBar';
import PortfolioStats from './PortfolioStats';
import {Col, Row} from 'react-bootstrap';

const InvestmentsView = () => {
    const [fireStages, setFireStages] = useState([]);
    const [snapshots, setSnapshots] = useState([]);

    useEffect(() => {
        fetch('/budget/portfolio/fire-stages')
            .then(res => res.json())
            .then(setFireStages)
            .catch(() => {});

        fetch('/budget/portfolio/snapshots')
            .then(res => res.json())
            .then(setSnapshots)
            .catch(() => {});
    }, []);

    const currentValue = snapshots.length ? snapshots[snapshots.length - 1].value : null;

    return (
        <Main body={
            <Col>
                <Row className="mt-3">
                    <Col>
                        <PortfolioStats snapshots={snapshots}/>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <h6 className="text-muted">Wartość portfela w czasie</h6>
                        <PortfolioChart fireStages={fireStages} snapshots={snapshots}/>
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
