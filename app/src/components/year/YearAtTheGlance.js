import React from "react";
import YearlyExpenses from "./YearlyExpenses";
import Main from "../main/Main";

const YearAtTheGlance = () => {

    let body = <>
        <YearlyExpenses/>
    </>;

    return <Main body={body}/>
}

export default YearAtTheGlance;