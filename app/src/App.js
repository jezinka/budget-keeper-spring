import React from 'react';
import './App.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'jquery/dist/jquery.min.js'
import 'bootstrap/dist/js/bootstrap.min.js'

// importing components from react-router-dom package
import {BrowserRouter as Router, Route, Routes,} from "react-router-dom";
import CurrentMonth from "./components/CurrentMonth";
import YearAtTheGlance from "./components/YearAtTheGlance";
import AnyPeriod from "./components/AnyPeriod";


const App = () => {
    return (
        <>
            <Router>
                <Routes>
                    <Route exact path="/" element={<CurrentMonth/>}/>
                    <Route path="/AnyPeriod" element={<AnyPeriod/>}/>
                    <Route path="/YearAtTheGlance" element={<YearAtTheGlance/>}/>
                </Routes>
            </Router>
        </>
    );
}

export default App;