import React from 'react';
import './App.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'jquery/dist/jquery.min.js'
import 'bootstrap/dist/js/bootstrap.min.js'

import {BrowserRouter as Router, Route, Routes,} from "react-router-dom";
import CurrentMonth from "./components/currentMonth/CurrentMonth";
import YearAtTheGlance from "./components/year/YearAtTheGlance";


const App = () => {
    return (
        <>
            <Router>
                <Routes>
                    <Route exact path="/" element={<CurrentMonth/>}/>
                    <Route path="/yearAtTheGlance" element={<YearAtTheGlance/>}/>
                </Routes>
            </Router>
        </>
    );
}

export default App;