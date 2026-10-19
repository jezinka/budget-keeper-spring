import {Layer, Rectangle, ResponsiveContainer, Sankey, Tooltip} from "recharts";
import {useEffect, useState} from "react";
import {handleError} from "../../Utils";

// Domyślny węzeł Sankeya w recharts nie ma podpisu, więc rysujemy własny.
// Węzły bez wyjść stoją przy prawej krawędzi, więc ich podpis idzie na lewo.
const SankeyNode = ({x, y, width, height, payload}) => {
    const isSink = payload.targetNodes.length === 0;
    return (
        <Layer>
            <Rectangle x={x} y={y} width={width} height={height} fill="#5470c6" fillOpacity={0.9}/>
            <text x={isSink ? x - 6 : x + width + 6}
                  y={y + height / 2}
                  textAnchor={isSink ? 'end' : 'start'}
                  dominantBaseline="middle"
                  fontSize={12}>
                {payload.name}
            </text>
        </Layer>
    );
};

// API podaje węzły po nazwie, recharts po indeksie. recharts zapętla się na cyklu
// (wystarczy A->A), więc odrzucamy linki do nieznanych węzłów i zamykające cykl.
export function toSankeyData(flow) {
    const index = new Map(flow.nodes.map((node, i) => [node.name, i]));
    const targets = flow.nodes.map(() => []);
    const reaches = (from, to) => {
        const stack = [from];
        const seen = new Set();
        while (stack.length) {
            const node = stack.pop();
            if (node === to) {
                return true;
            }
            if (!seen.has(node)) {
                seen.add(node);
                stack.push(...targets[node]);
            }
        }
        return false;
    };

    const links = [];
    flow.links.forEach(link => {
        const source = index.get(link.source);
        const target = index.get(link.target);
        if (source === undefined || target === undefined || reaches(target, source)) {
            return;
        }
        targets[source].push(target);
        links.push({source, target, value: Number(link.value)});
    });
    return {nodes: flow.nodes, links};
}

const SankeyComponent = ({year, month}) => {
    const [data, setData] = useState({nodes: [], links: []});

    useEffect(() => {
        let ignore = false;

        async function loadData() {
            const url = month == null
                ? `/budget/expenses/yearlyBudgetFlow?year=${year}`
                : `/budget/expenses/monthlyBudgetFlow?year=${year}&month=${month}`;
            const response = await fetch(url);
            if (!response.ok) {
                return handleError('Nie udało się pobrać przepływów budżetu');
            }
            const flow = await response.json();
            if (!ignore) {
                setData(toSankeyData(flow));
            }
        }

        loadData();
        return () => {
            ignore = true;
        };
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
