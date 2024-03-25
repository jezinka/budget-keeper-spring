import React from 'react';
import { Row, Col } from 'react-bootstrap';

const CategoryCheckboxRow = ({ categories, selectedCategories, setSelectedCategories }) => {
    return (
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
                        />
                        {category}
                    </Col>
                );
            })}
        </Row>
    );
};

export default CategoryCheckboxRow;