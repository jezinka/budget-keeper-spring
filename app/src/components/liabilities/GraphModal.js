import {Modal} from "react-bootstrap";
import {useState} from "react";
import {Line, LineChart, XAxis, YAxis} from "recharts";
import {DATE_FORMAT} from "../../Utils";

const GraphModal = ({liabilityId, showGraph, setShowGraph}) => {
    const [data, setData] = useState([])

    const loadData = async () => {
        const lookouts = await fetch("liabilityLookouts/getLookouts/" + liabilityId);
        const graphData = await lookouts.json()
        graphData.forEach((d) => {
            d.date = DATE_FORMAT.format(new Date(d.date))
        })
        setData(graphData);
    }

    const handleClose = () => setShowGraph(false);

    return <Modal show={showGraph} onHide={handleClose} onShow={loadData} size="lg">
        <Modal.Header closeButton>
            <Modal.Title>Wykres</Modal.Title>
        </Modal.Header>
        <Modal.Body>
            <LineChart width={500} height={400} data={data}>
                <XAxis dataKey="date" interval={0} height={110}
                       tick={{angle: -90, textAnchor: 'end', 'dominantBaseline': 'ideographic'}}/>
                <YAxis/>
                <Line type="monotone" dataKey="outcome" stroke="#8884d8" strokeWidth={2}/>
            </LineChart>
        </Modal.Body>
    </Modal>
}

export default GraphModal;