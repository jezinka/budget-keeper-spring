import React, {useEffect, useState} from 'react';
import {Col, Row} from 'react-bootstrap';
import {handleError} from "../../Utils";

const CategoryCheckboxRow = ({categories, selectedCategories, setSelectedCategories}) => {
    const [levelSelected, setLevelSelected] = useState([]);
    const [categoryLevels, setCategoryLevels] = useState([]);

    useEffect(() => {
        fetchCategories();
    }, []);

    async function fetchCategories() {
        const response = await fetch('/budget/categories/all');

        if (response.ok) {
            const data = await response.json();
            if (data) {
                const levels = {};
                data.forEach(c => {
                    if (c.level in levels) {
                        levels[c.level].push(c.name);
                    } else {
                        levels[c.level] = [c.name];
                    }
                });
                setCategoryLevels(levels);
                return setLevelSelected(Object.keys(levels));
            }
        }
        return handleError();
    }

    const levelDescription = {
        0: "Podstawa",
        1: "Dostatek",
        2: "Inwestycje",
        3: "Ponad"
    };

    return (<>
            <Row className={"mb-2"}>
                <Col sm={2}>
                    <input id="allCategories"
                           type="checkbox"
                           checked={selectedCategories.length === categories.length}
                           onChange={(event) => {
                               if (event.target.checked) {
                                   setSelectedCategories(categories)
                                   setLevelSelected(Object.keys(categoryLevels));
                               } else {
                                   setSelectedCategories([])
                                   setLevelSelected([]);
                               }
                           }}/><span className="fw-bold">&nbsp;Zaznacz/odznacz wszystkie</span>
                </Col>
                {Object.keys(categoryLevels)
                    .filter(l => l >= 0)
                    .map((level, index) => {
                        return (
                            <Col sm={2} key={level}>
                                <input
                                    type="checkbox"
                                    checked={levelSelected.indexOf(level) !== -1}
                                    onChange={(event) => {
                                        if (event.target.checked) {
                                            setLevelSelected([...levelSelected, level]);
                                            setSelectedCategories([...selectedCategories, ...categoryLevels[level]]);
                                        } else {
                                            setLevelSelected(levelSelected.filter(l => l !== level));
                                            let selectedSurvivors = [...selectedCategories];
                                            categoryLevels[level].forEach(unSelected => {
                                                selectedSurvivors = selectedSurvivors.filter(c => c !== unSelected);
                                            });
                                            setSelectedCategories(selectedSurvivors);
                                        }
                                    }}
                                /><span className="fw-bold">&nbsp;{levelDescription[level]}</span>
                            </Col>
                        )
                    })}
            </Row>
            <Row>
                {categories.map((category) => {
                    return (
                        <Col sm={2} key={category}>
                            <input
                                type="checkbox"
                                checked={selectedCategories.find(c => c === category) !== undefined}
                                onChange={(event) => {
                                    if (event.target.checked) {
                                        setSelectedCategories([...selectedCategories, category]);
                                    } else {
                                        setSelectedCategories(selectedCategories.filter(c => c !== category));
                                    }
                                }}
                            />&nbsp;{category}
                        </Col>
                    );
                })}
            </Row>
        </>
    );
};

export default CategoryCheckboxRow;