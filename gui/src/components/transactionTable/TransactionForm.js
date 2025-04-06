import React from "react";
import { Button, Col, Form, Row } from "react-bootstrap";
import { Plus } from "react-bootstrap-icons";

const TransactionForm = ({ formState, handleChange, handleSplit, splitFlow, getCategoriesMap, setShowCategoryForm }) => {
    return (
        <Form>
            <Row>
                <Col sm={3}>
                    <Form.Control className="m-2"
                                  placeholder="Kiedy:" type="date" disabled onChange={handleChange}
                                  name="transactionDate"
                                  value={formState.transactionDate} />
                </Col>
                <Col sm={8}>
                    <Form.Control className="m-2"
                                  placeholder="Co:" type="text" disabled onChange={handleChange}
                                  name="title"
                                  value={formState.title} />
                </Col>
                <Col sm={3}>
                    <Form.Control className="m-2" placeholder="Ile:" type="number" disabled
                                  name="baseSplitAmount" value={formState.baseSplitAmount} />
                </Col>
                <Col sm={{ span: 8 }}>
                    <Form.Control className="m-2"
                                  placeholder="Kto:" type="text" disabled onChange={handleChange}
                                  name="payee"
                                  value={formState.payee} />
                </Col>
            </Row>
            <Row className="align-items-center">
                <Col sm={3}>
                    {splitFlow ?
                        <Form.Control className="m-2" placeholder="Ile:" type="number"
                                      onChange={handleChange}
                                      name="amount" value={formState.amount} /> : ''}
                </Col>
                <Col sm={6}>
                    <Form.Select className="m-2" placeholder="Kategoria:" onChange={handleChange}
                                 name="categoryId" value={formState.categoryId}
                                 autoFocus>
                        {getCategoriesMap()}
                    </Form.Select>
                </Col>
                <Col sm={1}>
                    <Button size={"sm"} onClick={() => {
                        setShowCategoryForm(true);
                    }}> <Plus /> </Button>
                </Col>
            </Row>
            {splitFlow ? <Row>
                <Col sm={3}>
                    <Form.Control className="m-2" placeholder="Ile:" type="number"
                                  onChange={handleSplit}
                                  name="splitAmount" value={formState.splitAmount} />
                </Col>
                <Col sm={8}>
                    <Form.Select className="m-2" placeholder="Kategoria:" onChange={handleChange}
                                 name="splitCategoryId" value={formState.splitCategoryId}>
                        {getCategoriesMap()}
                    </Form.Select>
                </Col>
            </Row> : ''}
        </Form>
    );
};

export default TransactionForm;