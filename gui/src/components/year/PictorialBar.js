import {useEffect, useState} from "react";

const PictorialBar = ({year}) => {
    const [targetAmount, setTargetAmount] = useState(0);
    const [actualAmount, setActualAmount] = useState(0);
    const SQUARE_VALUE = 100;
    const COLUMNS = 100;

    useEffect(() => {
        loadInvestmentGoalPieData();
    }, [year]);

    async function loadInvestmentGoalPieData() {
        const response = await fetch("/budget/expenses/investmentGoalForYear?year=" + year);
        const data = await response.json();
        setTargetAmount(data.target || 0);
        setActualAmount(data.actual || 0);
    }

    const totalSquares = Math.ceil(Math.max(targetAmount, actualAmount) / SQUARE_VALUE);
    const filledSquares = Math.ceil(actualAmount / SQUARE_VALUE);
    const completion = targetAmount > 0 ? ((actualAmount / targetAmount) * 100).toFixed(1) : "0.0";

    return (
        <div>
            <div style={{
                display: "grid",
                gridTemplateColumns: `repeat(${COLUMNS}, 8px)`,
                gap: "1px",
                justifyContent: "start",
                marginBottom: "5px"
            }}>
                {Array.from({length: totalSquares}).map((_, index) => (
                    <div
                        key={index}
                        title={`${SQUARE_VALUE} zł`}
                        style={{
                            width: "8px",
                            height: "8px",
                            border: "1px solid #c6c6c6",
                            backgroundColor: index < filledSquares ? "#2e7d32" : "#ececec"
                        }}
                    />
                ))}
            </div>
            <div style={{fontSize: "14px"}}>
                Odłożono: <strong>{actualAmount.toLocaleString("pl-PL")} zł</strong> / Cel: {targetAmount.toLocaleString("pl-PL")} zł ({completion}%)
            </div>
            <div style={{fontSize: "12px", color: "#666"}}>1 kwadrat = {SQUARE_VALUE} zł</div>
        </div>
    );
}
export default PictorialBar