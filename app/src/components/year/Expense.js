import {SUM_MONTH, SUMMARY_STYLE} from "../../Utils";

const Expense = ({expense}) => {

    if (expense === undefined) {
        return <td>0.00</td>;
    }

    if (expense.month === SUM_MONTH) {
        return <td style={SUMMARY_STYLE}>{expense.amount}</td>
    }

    return <td>{expense.amount}</td>;

}

export default Expense;