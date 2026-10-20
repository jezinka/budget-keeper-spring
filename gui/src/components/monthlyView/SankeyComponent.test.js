import {sankeyHeight, sortByValue, toSankeyData} from "./SankeyComponent";

const flow = (links) => ({
    nodes: [{name: 'Dochody'}, {name: 'Życie'}, {name: 'Jedzenie'}],
    links: links.map(([source, target, value]) => ({source, target, value}))
});

test('maps node names to indices and string amounts to numbers', () => {
    const data = toSankeyData(flow([['Dochody', 'Życie', '12.50'], ['Życie', 'Jedzenie', '10']]));

    expect(data.links).toEqual([{source: 0, target: 1, value: 12.5}, {source: 1, target: 2, value: 10}]);
});

test('drops self links, cycle-closing links and links to unknown nodes', () => {
    const data = toSankeyData(flow([
        ['Dochody', 'Życie', '10'],
        ['Życie', 'Życie', '1'],
        ['Życie', 'Dochody', '2'],
        ['Życie', 'Nieznany', '3'],
        ['Życie', 'Jedzenie', '4'],
        ['Jedzenie', 'Dochody', '5']
    ]));

    expect(data.links).toEqual([{source: 0, target: 1, value: 10}, {source: 1, target: 2, value: 4}]);
});

test('grows with the fullest column, counting sinks in the last one', () => {
    const nodes = [{name: 'Dochody'}, {name: 'Życie'}, {name: 'Dom'}, ...Array.from({length: 20}, (_, i) => ({name: `k${i}`}))];
    // Dom nie ma dzieci, więc recharts stawia go w ostatniej kolumnie razem z 20 kategoriami.
    const links = [{source: 0, target: 1}, {source: 0, target: 2}, ...Array.from({length: 20}, (_, i) => ({source: 1, target: i + 3}))];

    expect(sankeyHeight({nodes, links})).toBe(21 * 30);
    expect(sankeyHeight({nodes: nodes.slice(0, 3), links: links.slice(0, 2)})).toBe(400);
});

test('orders nodes largest first, grouping the lowest level by parent', () => {
    const data = toSankeyData({
        nodes: ['Dochody', 'Dom', 'Życie', 'czynsz', 'prąd', 'jedzenie', 'kino'].map(name => ({name})),
        links: [
            {source: 'Dochody', target: 'Dom', value: 300},
            {source: 'Dochody', target: 'Życie', value: 500},
            {source: 'Dom', target: 'prąd', value: 100},
            {source: 'Dom', target: 'czynsz', value: 200},
            {source: 'Życie', target: 'kino', value: 50},
            {source: 'Życie', target: 'jedzenie', value: 450},
        ]
    });

    const sorted = sortByValue(data);

    expect(sorted.nodes.map(node => node.name)).toEqual(['Dochody', 'Życie', 'Dom', 'jedzenie', 'kino', 'czynsz', 'prąd']);
    expect(sorted.links.map(({source, target}) => [sorted.nodes[source].name, sorted.nodes[target].name]))
        .toContainEqual(['Dom', 'czynsz']);
});
