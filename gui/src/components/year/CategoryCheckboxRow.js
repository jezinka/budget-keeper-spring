import React from 'react';
import {Col, Row} from 'react-bootstrap';

const CategoryCheckboxRow = ({categories, selectedCategories, setSelectedCategories}) => {
    return (
        <Row>
            <Col sm={2}>
                <input type="checkbox"
                       checked={selectedCategories.length === categories.length}
                       onChange={(event) => {
                           if (event.target.checked) {
                               setSelectedCategories(categories)
                           } else {
                               setSelectedCategories([])
                           }
                       }}/><span className="fw-bold">&nbsp;Zaznacz/odznacz wszystkie</span>
            </Col>
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
    );
};

export default CategoryCheckboxRow;