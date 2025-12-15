import React from "react";
import {Button, Col, Form, Row} from "react-bootstrap";
import {Plus} from "react-bootstrap-icons";

const TransactionForm = ({
                             formState,
                             handleChange,
                             handleSplit,
                             splitFlow,
                             getCategoriesMap,
                             setShowCategoryForm,
                             editable = false
                         }) => {
    return (
        <Form>
            {splitFlow ? '' :
                <Row className="align-items-center">
                    <Col sm={11}><Form.Control className={"m-2"} placeholder="Opis:"
                                               type="text"
                                               onChange={handleChange}
                                               name="note"
                                               value={formState.note}/>
                    </Col>
                </Row>}
            <Row>
                <Col sm={3}>
                    <Form.Control className="m-2"
                                  placeholder="Kiedy:" type="date" onChange={handleChange}
                                  name="transactionDate"
                                  value={formState.transactionDate}
                                  disabled={!editable}/>
                </Col>
                <Col sm={8}>
                    <Form.Control className="m-2"
                                  placeholder="Co:" type="text" onChange={handleChange}
                                  name="title"
                                  value={formState.title}
                                  disabled={!editable}/>
                </Col>
                <Col sm={3}>
                    <Form.Control className="m-2" placeholder="Ile:" type="number"
                                  name="baseSplitAmount" value={formState.baseSplitAmount}
                                  onChange={handleChange} disabled={!editable}/>
                </Col>
                <Col sm={{span: 8}}>
                    <Form.Control className="m-2"
                                  placeholder="Kto:" type="text" onChange={handleChange}
                                  name="payee"
                                  value={formState.payee}
                                  disabled={!editable}/>
                </Col>
            </Row>
            <Row className="align-items-center">
                <Col sm={3}>
                    {splitFlow ?
                        <Form.Control className="m-2" placeholder="Ile:" type="number"
                                      onChange={handleChange}
                                      name="amount" value={formState.amount}/> : ''}
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
                    }}> <Plus/> </Button>
                </Col>
            </Row>
            {splitFlow ? <Row>
                <Col sm={3}>
                    <Form.Control className="m-2" placeholder="Ile:" type="number"
                                  onChange={handleSplit}
                                  name="splitAmount" value={formState.splitAmount}/>
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
