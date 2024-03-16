import React from 'react';
import './App.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'jquery/dist/jquery.min.js'
import 'bootstrap/dist/js/bootstrap.min.js'

import {BrowserRouter as Router, Route, Routes,} from "react-router-dom";
import CurrentMonth from "./components/currentMonth/CurrentMonth";
import YearAtTheGlance from "./components/year/YearAtTheGlance";
import AllTransactions from "./components/allTransactions/AllTransactions";
import YearAtTheGlanceBarChart from "./components/year/YearAtTheGlanceBarChart";
import YearAtTheGlancePieChart from "./components/year/YearAtTheGlancePieChart";
import LogsView from "./components/logs/LogsView";
import FixedCosts from "./components/fixedCosts/FixedCosts";

const App = () => {
    return (
        <>
            <Router>
                <Routes>
                    <Route exact path="/" element={<CurrentMonth/>}/>
                    <Route path="/gui/yearAtTheGlance" element={<YearAtTheGlance/>}/>
                    <Route path="/gui/yearAtTheGlanceBarChart" element={<YearAtTheGlanceBarChart/>}/>
                    <Route path="/gui/yearAtTheGlancePieChart" element={<YearAtTheGlancePieChart/>}/>
                    <Route path="/gui/allTransactions" element={<AllTransactions/>}/>
                    <Route path="/gui/fixedCosts" element={<FixedCosts/>}/>
                    <Route path="/gui/logs" element={<LogsView/>}/>
                </Routes>
            </Router>
        </>
    );
}

export default App;