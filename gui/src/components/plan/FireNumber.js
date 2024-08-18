import React, {useEffect, useState} from 'react';
import {formatNumber} from "../../Utils";
import Table from "react-bootstrap/Table";

function FireNumber() {
    const [fireData, setFireData] = useState(0);

    useEffect(() => {
        async function fetchFireData() {
            try {
                const response = await fetch('/budget/expenses/fireNumber');
                if (response.ok) {
                    const data = await response.json();
                    setFireData(data);
                } else {
                    console.error('Failed to fetch fire number');
                }
            } catch (error) {
                console.error('Error fetching fire number:', error);
            }
        }

        fetchFireData();
    }, []);

    return (
        <Table responsive='sm' striped bordered size="sm">
            <tbody>
            <tr>
                <td className='table-info'>FIRE</td>
                <td>{formatNumber(fireData.fireNumber)}</td>
            </tr>
            <tr>
                <td className='table-info'>Zainwestowano</td>
                <td>{formatNumber(fireData.investmentSum) + " ("+ fireData.percent + "%)"}</td>
            </tr>
            </tbody>
        </Table>
    );
}

export default FireNumber;