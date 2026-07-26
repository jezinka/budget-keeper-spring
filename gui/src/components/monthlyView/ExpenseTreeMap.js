import {ResponsiveContainer, Tooltip, Treemap} from 'recharts';
import {monthColors} from "../../Utils";

function renderCustomizedTreemapNode(props) {
    const {x = 0, y = 0, width = 0, height = 0, depth = 0, index =0, name } = props;

    if (width <= 1 || height <= 1) {
        return <g/>;
    }

    const canRenderLabel = width > 72 && height > 26;
    const fontSize = depth === 0 ? 15 : 12;
    const strokeWidth = depth === 0 ? 2 : 1;

    return (
        <g>
            <rect
                x={x}
                y={y}
                width={width}
                height={height}
                fill={monthColors[index % monthColors.length]}
                stroke="var(--color-surface-base)"
                strokeWidth={strokeWidth}
                rx={4}
            />
            {canRenderLabel ? (
                <text x={x + 8} y={y + 18} fill="#fff" fontSize={fontSize} fontWeight={depth === 0 ? 700 : 500}>
                    {name}
                </text>
            ) : null}
            {canRenderLabel ? (
                <text x={x + 8} y={y + 34} fill="rgba(255, 255, 255, 0.85)" fontSize={11}>
                    {Number.parseFloat(props.value).toFixed(2)}
                </text>
            ) : null}
        </g>
    );
}

const ExpenseTreeMap = ({expenses}) => {
    return (<ResponsiveContainer width="100%" height={300}>
        <Treemap
            isAnimationActive={false}
            style={{width: '100%', maxWidth: '100px', maxHeight: '40vh', aspectRatio: 4 / 3}}
            data={expenses}
            dataKey="amount"
            nameKey="name"
            aspectRatio={4 / 3}
            type="nest"
            content={renderCustomizedTreemapNode}
            stroke="#fff"
            fill="#8884d8">
            <Tooltip/>
        </Treemap>
    </ResponsiveContainer>);
};

export default ExpenseTreeMap;