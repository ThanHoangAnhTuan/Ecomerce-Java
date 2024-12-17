import HeaderServer from "@/app/components/HeaderServer";

import React from "react";
import OrderDetailsClientPage from "@/app/order-details/OrderDetailsClientPage";

const OrderDetails = () => {
    return (
        <>
            <HeaderServer></HeaderServer>
            <OrderDetailsClientPage/>
        </>
    )
}

export default OrderDetails