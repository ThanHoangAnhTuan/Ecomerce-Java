import {Suspense} from "react";
import ActiveAccountClient from "@/app/(auth)/active-account/ClientActiveAccount";


const ActiveAccountServer = () => {
    return (
        <Suspense>
            <ActiveAccountClient/>
        </Suspense>
    )
}

export default ActiveAccountServer