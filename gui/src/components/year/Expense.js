import {formatNumber, SUM_CATEGORY, SUM_MONTH} from "../../Utils";
import Table from "react-bootstrap/Table";
import Tooltip from "react-bootstrap/Tooltip";
import OverlayTrigger from "react-bootstrap/OverlayTrigger";

const Expense = ({expense, year, modalHandler, modalContentHandler, selectCurrentMonth = true}) => {

    const renderTooltip = async () => {
        if (expense.month === SUM_MONTH || expense.month === SUM_CATEGORY || expense.transactionCount === 0) {
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

    let className = "";

    if (expense.transactionCount === undefined || expense.transactionCount === 0) {
        className += "no-transactions ";
    }

    if (selectCurrentMonth && year == new Date().getFullYear() && (new Date().getMonth() + 1) === expense.month) {
        className += "current-month ";
        if (expense.category === SUM_CATEGORY) {
            className += "current-month-footer ";
        }
    }

    if (modalHandler === undefined || modalContentHandler === undefined) {
        return (<td className={className} style={{textAlign: 'right'}}>{formatNumber(expense.amount)}</td>);
    }

    if (expense.goalAmount !== null) {
        className += "triangle " + (expense.goalAmount <= expense.amount ? "success" : "fail");
        return (<OverlayTrigger
                placement={'top-end'}
                overlay={<Tooltip id={`tooltip-cell-${expense.id}`}>
                    {formatNumber(expense.goalAmount)}
                </Tooltip>}>
                <td className={className} style={{textAlign: 'right'}}
                    onClick={renderTooltip}>{formatNumber(expense.amount)}</td>
            </OverlayTrigger>
        );
    }

    return (
        <td className={className} style={{textAlign: 'right'}}
            onClick={renderTooltip}>{formatNumber(expense.amount)}</td>
    );
}

export default Expense;