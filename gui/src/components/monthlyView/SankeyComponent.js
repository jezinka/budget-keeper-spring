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

const NODE_PADDING = 20;
const ROW_HEIGHT = NODE_PADDING + 10;

// Kolumna węzła tak jak liczy ją recharts: najdłuższa ścieżka od źródła,
// a węzły bez wyjść (align "justify") lądują w ostatniej kolumnie.
function columnsOf({nodes, links}) {
    const depth = nodes.map(() => 0);
    for (let changed = true; changed;) {
        changed = false;
        links.forEach(({source, target}) => {
            if (depth[target] < depth[source] + 1) {
                depth[target] = depth[source] + 1;
                changed = true;
            }
        });
    }
    const lastColumn = Math.max(...depth);
    const hasOutgoing = new Set(links.map(link => link.source));
    return depth.map((d, i) => hasOutgoing.has(i) ? d : lastColumn);
}

// recharts najpierw odejmuje odstępy między węzłami od wysokości, więc przy stałej wysokości
// i wielu kategoriach w kolumnie węzły kurczą się do zera i podpisy wchodzą na siebie.
export function sankeyHeight(data) {
    const counts = {};
    columnsOf(data).forEach(column => counts[column] = (counts[column] ?? 0) + 1);
    return Math.max(400, Math.max(...Object.values(counts)) * ROW_HEIGHT);
}

// Układa węzły od największego do najmniejszego w każdej kolumnie; w kolejnych kolumnach
// najpierw grupuje po rodzicu (żeby linki się nie przeplatały), a w grupie sortuje malejąco.
// Działa razem z sort={false} i verticalAlign="top", bo inaczej recharts sam przestawia węzły.
export function sortByValue(data) {
    const {nodes, links} = data;
    const columns = columnsOf(data);
    const inflow = nodes.map(() => 0);
    const outflow = nodes.map(() => 0);
    links.forEach(({source, target, value}) => {
        outflow[source] += value;
        inflow[target] += value;
    });
    const value = i => Math.max(inflow[i], outflow[i]);
    const rank = new Map();
    const parentRank = i => Math.min(...links.filter(link => link.target === i).map(link => rank.get(link.source) ?? Infinity));

    for (let column = 0; column <= Math.max(...columns); column++) {
        nodes.map((node, i) => i)
            .filter(i => columns[i] === column)
            .sort((a, b) => parentRank(a) - parentRank(b) || value(b) - value(a))
            .forEach(i => rank.set(i, rank.size));
    }
    const order = [...rank.keys()];
    const newIndex = new Map(order.map((oldIndex, i) => [oldIndex, i]));
    return {
        nodes: order.map(i => nodes[i]),
        links: links.map(link => ({...link, source: newIndex.get(link.source), target: newIndex.get(link.target)}))
    };
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
                setData(sortByValue(toSankeyData(flow)));
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
        <ResponsiveContainer width="100%" height={sankeyHeight(data)}>
            <Sankey data={data} node={<SankeyNode/>} nodePadding={NODE_PADDING} sort={false} verticalAlign="top"
                    margin={{top: 10, right: 10, bottom: 10, left: 10}}
                    link={{stroke: '#91cc75', strokeOpacity: 0.4}}>
                <Tooltip/>
            </Sankey>
        </ResponsiveContainer>
    );
};

export default SankeyComponent;
