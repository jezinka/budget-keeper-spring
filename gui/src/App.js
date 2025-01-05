import React from 'react';
import './App.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'jquery/dist/jquery.min.js'
import 'bootstrap/dist/js/bootstrap.min.js'

import {BrowserRouter as Router, Route, Routes,} from "react-router-dom";
import CurrentMonth from "./components/currentMonth/CurrentMonth";
import YearAtTheGlance from "./components/year/YearAtTheGlance";
import AllTransactions from "./components/allTransactions/AllTransactions";
import LogsView from "./components/logs/LogsView";
import Plan from "./components/plan/Plan";
import LifestyleInflation from "./components/lifestyleInflation/LifestyleInflation";

const App = () => {
    return (
        <>
            <Router basename="/budget">
                <Routes>
                    <Route exact path="/" element={<CurrentMonth/>}/>
                    <Route path="/gui/yearAtTheGlance" element={<YearAtTheGlance/>}/>
                    <Route path="/gui/allTransactions" element={<AllTransactions/>}/>
                    <Route path="/gui/plan" element={<Plan/>}/>
                    <Route path="/gui/lifestyleInflation" element={<LifestyleInflation/>}/>
                    <Route path="/gui/logs" element={<LogsView/>}/>
                </Routes>
            </Router>
        </>
    );
}

export default App;