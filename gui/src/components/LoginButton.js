import React, {useContext} from "react";
import {UserContext} from "../context/UserContext";
import {Button} from "react-bootstrap";

export default function LoginButton() {
    const {user} = useContext(UserContext);

    if (user) return null;

    const handleLogin = () => {
        window.location.href = "/budget/oauth2/authorization/google";
    };

    return (<>
            <Button variant="info" size={"sm"} className={"mx-sm-2"} onClick={handleLogin}>
                Zaloguj
            </Button>
            <hr/>
        </>
    );
}