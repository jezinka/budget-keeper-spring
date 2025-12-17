import React, {useEffect, useState} from 'react';
import {Col, Row} from 'react-bootstrap';
import {handleError} from "../../Utils";

const CategoryCheckboxRow = ({categories, selectedCategories, setSelectedCategories}) => {
    const [levelSelected, setLevelSelected] = useState([]);
    const [categoryLevels, setCategoryLevels] = useState([]);
    const [levelDescription, setLevelDescription] = useState([]);

    useEffect(() => {
        fetchCategories();
    }, [categories]);

    useEffect(() => {
        fetchLevelDescription();
    }, []);

    async function fetchCategories() {

        const levels = {};
        categories.forEach(c => {
            if (c.level in levels) {
                levels[c.level].push(c.name);
            } else {
                levels[c.level] = [c.name];
            }
        });
        setCategoryLevels(levels);
        return setLevelSelected(Object.keys(levels));
    }

    async function fetchLevelDescription() {
        const response = await fetch('/budget/categories/levels');

        if (response.ok) {
            const data = await response.json();
            if (data) {
                return setLevelDescription(data);
            }
        }
        return handleError();
    }

    return (<>
            <Row className={"mb-2"}>
                <Col sm={2}>
                    <input id="allCategories"
                           type="checkbox"
                           checked={selectedCategories.length === categories.length}
                           onChange={(event) => {
                               if (event.target.checked) {
                                   setSelectedCategories(categories.map(c => c.name))
                                   setLevelSelected(Object.keys(categoryLevels));
                               } else {
                                   setSelectedCategories([])
                                   setLevelSelected([]);
                               }
                           }}/><span className="fw-bold">&nbsp;Zaznacz/odznacz wszystkie</span>
                </Col>
            </Row>
            <Row className={"mb-2"}>
                {Object.keys(categoryLevels)
                    .filter(l => l >= 0)
                    .map((level) => {
                        let levels = levelDescription.filter(l => l.level == level);
                        let levelName;
                        if (levels.length !== 0) {
                            levelName = levels[0].name;
                        }
                        return (
                            <Col sm={1} key={level}>
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
                                />
                                <span
                                    className="fw-bold">&nbsp;{levelName}</span>
                            </Col>
                        )
                    })}
            </Row>
            <Row>
                {categories.map(c => c.name).map((category) => {
                    return (
                        <Col sm={1} key={category}>
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