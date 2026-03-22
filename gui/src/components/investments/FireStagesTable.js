import React from 'react';
import {Table} from 'react-bootstrap';
import {CheckCircleFill, Circle} from 'react-bootstrap-icons';
import {formatNumber} from '../../Utils';

const formatDate = (dateStr) => {
    if (!dateStr) return '—';
    const [year, month, day] = dateStr.split('-');
    return `${day}.${month}.${year}`;
};

const FireStagesTable = ({fireStages = []}) => {

    if (fireStages.length === 0) return null;

    const crossed = fireStages.filter(s => s.firstCrossedAt).length;

    return (
        <>
            <h5 className="mt-4">Etapy FIRE
                <small className="text-muted ms-2" style={{fontSize: '0.75rem'}}>
                    {crossed} / {fireStages.length} przekroczone
                </small>
            </h5>
            <Table striped bordered size="sm" style={{maxWidth: 420}}>
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Próg (PLN)</th>
                        <th>Data przekroczenia</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    {fireStages.map((stage, i) => {
                        const done = !!stage.firstCrossedAt;
                        return (
                            <tr key={stage.id} style={done ? {} : {color: '#adb5bd'}}>
                                <td>{i + 1}</td>
                                <td>{formatNumber(stage.threshold)}</td>
                                <td>{formatDate(stage.firstCrossedAt)}</td>
                                <td style={{textAlign: 'center'}}>
                                    {done
                                        ? <CheckCircleFill color="#198754"/>
                                        : <Circle color="#dee2e6"/>}
                                </td>
                            </tr>
                        );
                    })}
                </tbody>
            </Table>
        </>
    );
};

export default FireStagesTable;
