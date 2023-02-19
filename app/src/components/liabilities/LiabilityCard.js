import Card from 'react-bootstrap/Card';
import {Button} from "react-bootstrap";
import {GraphUp, PlusLg} from "react-bootstrap-icons";
import {DATE_FORMAT, formatNumber, getDate} from "../../Utils";

function LiabilityCard({liability, formState, setShowForm, setFormState, setShowGraph}) {

    async function addOutcome(id) {
        setFormState({"liabilityId": id, "date": getDate(), outcome: 0});
        setShowForm(true);
    }

    function showGraph(id) {
        setFormState({...formState, "liabilityId": id});
        setShowGraph(true);
    }

    return (<>
        <Card style={{width: '18rem'}} className="m-2">
            <Card.Header>{liability.bankName}</Card.Header>
            <Card.Body>
                <Card.Title>{liability.liabilityName}</Card.Title>
                <Card.Subtitle className="mb-2 text-muted">{formatNumber(liability.outcome)}</Card.Subtitle>
                <Button size='sm' variant="primary" className="m-1"
                        onClick={() => addOutcome(liability.liabilityId)}><PlusLg/></Button>
                <Button size='sm' variant="primary" className="m-1"
                        onClick={() => showGraph(liability.liabilityId)}><GraphUp/></Button>
            </Card.Body>
            <Card.Footer>{DATE_FORMAT.format(new Date(liability.date))}</Card.Footer>
        </Card>
    </>);
}

export default LiabilityCard;