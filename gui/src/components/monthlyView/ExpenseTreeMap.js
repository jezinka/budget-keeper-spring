import {ResponsiveContainer, Treemap} from 'recharts';

function renderCustomizedTreemapNode(props) {
    const { x = 0, y = 0, width = 0, height = 0, depth = 0, name, fill = '#8884d8' } = props;

    if (width <= 1 || height <= 1) {
        return <g />;
    }

    const canRenderLabel = width > 72 && height > 26;
    const canRenderValue = width > 112 && height > 42;
    const fontSize = depth === 0 ? 15 : 12;
    const strokeWidth = depth === 0 ? 2 : 1;

    return (
        <g>
            <rect
                x={x}
                y={y}
                width={width}
                height={height}
                fill={fill}
                stroke="var(--color-surface-base)"
                strokeWidth={strokeWidth}
                rx={4}
            />
            {canRenderLabel ? (
                <text x={x + 8} y={y + 18} fill="#fff" fontSize={fontSize} fontWeight={depth === 0 ? 700 : 500}>
                    {name}
                </text>
            ) : null}
            {canRenderValue ? (
                <text x={x + 8} y={y + 34} fill="rgba(255, 255, 255, 0.85)" fontSize={11}>
                    {props.value}
                </text>
            ) : null}
        </g>
    );
}

const ExpenseTreeMap = ({expenses}) => {
    return (<ResponsiveContainer width="100%" height={300}>
        <Treemap
            isAnimationActive={false}
            style={{width: '100%', maxWidth: '500px', maxHeight: '80vh', aspectRatio: 1/2}}
            data={expenses}
            dataKey="amount"
            nameKey="name"
            aspectRatio={1/2}
            type="nest"
            content={renderCustomizedTreemapNode}
            stroke="#fff"
            fill="#8884d8">
        </Treemap>
    </ResponsiveContainer>);
};

export default ExpenseTreeMap;