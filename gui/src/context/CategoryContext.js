import React, {createContext, useEffect, useState} from "react";
import {handleError} from "../Utils";

export const CategoryContext = createContext();

export const CategoryProvider = ({children}) => {
    const [categories, setCategories] = useState([]);

    useEffect(() => {
        fetchCategories();
    }, []);

    async function fetchCategories() {
        const response = await fetch('/budget/categories/all');
        if (response.ok) {
            const data = await response.json();
            if (data) {
                setCategories(data);
            }
        } else {
            handleError();
        }
    }

    return (
        <CategoryContext.Provider value={{categories, fetchCategories}}>
            {children}
        </CategoryContext.Provider>
    );
};