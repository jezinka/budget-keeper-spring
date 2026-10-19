import {Layer, Rectangle, ResponsiveContainer, Sankey, Tooltip} from "recharts";
import {useEffect, useState} from "react";

// Domyślny węzeł Sankeya w recharts nie ma podpisu, więc rysujemy własny.
const SankeyNode = ({x, y, width, height, payload, containerWidth}) => {
    const isRight = x + width + 6 > containerWidth - 150;
    return (
        <Layer>
            <Rectangle x={x} y={y} width={width} height={height} fill="#5470c6" fillOpacity={0.9}/>
            <text x={isRight ? x - 6 : x + width + 6}
                  y={y + height / 2}
                  textAnchor={isRight ? 'end' : 'start'}
                  dominantBaseline="middle"
                  fontSize={12}>
                {payload.name}
            </text>
        </Layer>
    );
};

const SankeyComponent = ({year, month}) => {
    const [data, setData] = useState({nodes: [], links: []});

    useEffect(() => {

        async function loadData() {
            let response;
            if (month == null) {
                response = await fetch(`/budget/expenses/yearlyBudgetFlow?year=${year}`);
            } else {
                response = await fetch(`/budget/expenses/monthlyBudgetFlow?year=${year}&month=${month}`);
            }
            const flow = await response.json();
            // recharts wskazuje węzły po indeksie, API po nazwie
            const index = new Map(flow.nodes.map((node, i) => [node.name, i]));
            setData({
                nodes: flow.nodes,
                links: flow.links.map(link => ({
                    source: index.get(link.source),
                    target: index.get(link.target),
                    value: Number(link.value)
                }))
            });
        }

        loadData();
    }, [year, month]);

    if (data.links.length === 0) {
        return null;
    }

    return (
        <ResponsiveContainer width="100%" height={400}>
            <Sankey data={data} node={<SankeyNode/>} nodePadding={20}
                    margin={{top: 10, right: 10, bottom: 10, left: 10}}
                    link={{stroke: '#91cc75', strokeOpacity: 0.4}}>
                <Tooltip/>
            </Sankey>
        </ResponsiveContainer>
    );
};

export default SankeyComponent;
