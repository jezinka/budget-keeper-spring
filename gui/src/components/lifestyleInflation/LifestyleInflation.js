import React, {useEffect, useState} from "react";
import Main from "../main/Main";
import {CartesianGrid, Legend, Line, LineChart, Tooltip, XAxis, YAxis} from "recharts";
import {Col, Row} from "react-bootstrap";
import {handleError, monthColors} from "../../Utils";

const LifestyleInflation = () => {
    const [data, setData] = useState([]);
    const [categories, setCategories] = useState([]);
    const [selectedCategories, setSelectedCategories] = useState([]);
    const [levelSelected, setLevelSelected] = useState([]);
    const [categoryLevels, setCategoryLevels] = useState([]);
    const [levelDescription, setLevelDescription] = useState([]);

    useEffect(() => {
        loadData();
        fetchCategories();
        fetchLevelDescription();
    }, []);

    useEffect(() => {
        setCategoriesLevels();
    }, [categories]);

    async function fetchCategories() {
        const response = await fetch('/budget/categories/onlyExpenses');

        if (response.ok) {
            const data = await response.json();
            if (data) {
                setCategories(data);
                setSelectedCategories(data.map(c => c.name));
            }
        } else {
            return handleError();
        }
    }

    async function setCategoriesLevels() {
        const levels = {};
        categories.forEach(c => {
            if (c.level in levels) {
                levels[c.level].push(c.name);
            } else {
                levels[c.level] = [c.name];
            }
        });
        setCategoryLevels(levels);
        setLevelSelected(Object.keys(levels));
    }

    async function fetchLevelDescription() {
        const response = await fetch('/budget/categories/levels');

        if (response.ok) {
            const data = await response.json();
            if (data) {
                setLevelDescription(data);
            }
        } else {
            handleError();
        }
    }

    async function loadData() {
        const response = await fetch('/budget/expenses/lifestyleInflation');
        if (response.ok) {
            let data = await response.json();
            let sorted = data.data.sort(function (a, b) {
                return ('' + a.date).localeCompare(b.date);
            })
            return setData(sorted);
        }
        handleError();
    }

    const CustomTooltip = ({active, payload, label}) => {
        if (active && payload && payload.length) {
            // Sort the payload by value (amount) in descending order
            const sortedPayload = [...payload].sort((a, b) => a.value - b.value);

            return (
                <div className="custom-tooltip">
                    <p className="label">{`${label}`}</p>
                    {sortedPayload.map((entry, index) => (
                        <p key={`item-${index}`} style={{color: entry.color}}>
                            {`${entry.name} : ${entry.value}`}
                        </p>
                    ))}
                </div>
            );
        }
    }

    const handleCategoryChange = (category, isChecked) => {
        if (isChecked) {
            setSelectedCategories([...selectedCategories, category]);
        } else {
            setSelectedCategories(selectedCategories.filter(c => c !== category));
        }
    };

    const handleLevelChange = (level, isChecked) => {
        if (isChecked) {
            setLevelSelected([...levelSelected, level]);
            setSelectedCategories([...selectedCategories, ...categoryLevels[level]]);
        } else {
            setLevelSelected(levelSelected.filter(l => l !== level));
            const updatedSelectedCategories = selectedCategories.filter(c => !categoryLevels[level].includes(c));
            setSelectedCategories(updatedSelectedCategories);
        }
    };

    function getSeries() {
        if (selectedCategories.length === categories.length) {
            let color = monthColors[0];
            return <Line connectNulls key="allCategories"
                         type="monotone" dataKey={"allCategories"}
                         fill={color} stroke={color}/>;
        }
        if (levelSelected.length > 0) {
            const series = levelSelected.map((l, index) => {
                    let color = monthColors[index % 12];
                    return <Line connectNulls key={index}
                                 type="monotone" dataKey={getLevelName(l)}
                                 fill={color} stroke={color}/>;
                }
            );
            const selectedInLevels = categories
                .filter(c => levelSelected.includes(c.level != null ? c.level.toString() : null))
                .map(c => c.name);

            selectedCategories
                .filter(sc => !selectedInLevels.includes(sc))
                .forEach((c, index) => {
                    let color = monthColors[(index + levelSelected.length) % 12];
                    series.push(<Line connectNulls key={"cat_" + index}
                                      type="monotone" dataKey={c}
                                      fill={color} stroke={color}/>);
                });
            return series;
        }
        return selectedCategories.map((c, index) => {
            let color = monthColors[index % 12];
            return <Line connectNulls key={index}
                         type="monotone" dataKey={c}
                         fill={color} stroke={color}/>;
        });
    }

    function getLevelName(level) {
        return levelDescription.find(l => l.level == level)?.name || `Level ${level}`;
    }

    let body = <>

        <Row className={"mt-5 mx-2"}>
            <Row className={"mb-2"}>
                <Col sm={2}>
                    <input
                        id="allCategories"
                        type="checkbox"
                        checked={selectedCategories.length === categories.length}
                        onChange={(event) => {
                            if (event.target.checked) {
                                setSelectedCategories(categories.map(c => c.name));
                                setLevelSelected(Object.keys(categoryLevels));
                            } else {
                                setSelectedCategories([]);
                                setLevelSelected([]);
                            }
                        }}
                    />
                    <span className="fw-bold">&nbsp;Select/Deselect All</span>
                </Col>
                {Object.keys(categoryLevels)
                    .filter(l => l >= 0)
                    .map((level) => {
                        const levelName = getLevelName(level);
                        return (
                            <Col sm={2} key={level}>
                                <input
                                    id={levelName}
                                    type="checkbox"
                                    checked={levelSelected.includes(level)}
                                    onChange={(event) => handleLevelChange(level, event.target.checked)}
                                />
                                <span className="fw-bold">&nbsp;{levelName}</span>
                            </Col>
                        );
                    })}
            </Row>
            <Row>
                {categories.map(c => (
                    <Col sm={2} key={c.name}>
                        <input
                            type="checkbox"
                            checked={selectedCategories.includes(c.name)}
                            onChange={(event) => handleCategoryChange(c.name, event.target.checked)}
                        />
                        &nbsp;{c.name}
                    </Col>
                ))}
            </Row>
        </Row>
        <Col>
            <LineChart width={1600} height={500} data={data}
                       margin={{top: 30, right: 30, left: 20, bottom: 5}}>
                <CartesianGrid strokeDasharray="3 3"/>
                <XAxis dataKey="date"/>
                <YAxis reversed={true}/>
                <Tooltip content={<CustomTooltip/>}
                         wrapperStyle={{
                             backgroundColor: "white", borderStyle: "ridge", paddingLeft: "10px", paddingRight: "10px"
                         }}
                />
                <Legend/>
                {getSeries()}
            </LineChart>
        </Col>
    </>;

    return <Main body={body}/>
}

export default LifestyleInflation;