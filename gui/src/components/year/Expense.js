import {formatNumber, SUM_CATEGORY, SUM_MONTH} from "../../Utils";
import Table from "react-bootstrap/Table";

const Expense = ({expense, year, modalHandler, modalContentHandler}) => {

    const renderTooltip = async () => {
        if (expense.month === SUM_MONTH || expense.month === SUM_CATEGORY) {
            return
        }

        const response = await fetch('/budget/expenses', {
            method: "POST",
            body: JSON.stringify({month: expense.month, year: year, category: expense.category}),
            headers: {'Content-Type': 'application/json'},
        });
        const data = await response.json()
        modalHandler();
        modalContentHandler(<Table>
            <tbody>
            {data.map((row) => {
                return <tr key={row.title + row.transactionDate}>
                    <td style={{minWidth: '80px'}}>{row.transactionDate}</td>
                    <td>{row.title}</td>
                    <td style={{textAlign: 'right'}}>{formatNumber(row.amount)}</td>
                </tr>
            })}

            </tbody>
        </Table>)
    };

    return (
        <td style={{textAlign: 'right'}} onClick={renderTooltip}>{formatNumber(expense.amount)}</td>
    );
}

export default Expense;