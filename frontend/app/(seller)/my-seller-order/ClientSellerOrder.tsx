"use client"

import {DataTable} from "@/app/(seller)/my-seller-order/components/data-table";
import * as React from "react";
import {getColumns} from "@/app/(seller)/my-seller-order/components/columns";
import {EPayment, IOrderResponse} from "@/app/types/types";
import {useEffect, useState} from "react";
import axios from "axios";

const SellerClient = () => {
    const [orderList, setOrderList] = useState<IOrderResponse[]>([])

    useEffect(() => {
        const fetchDataProduct = async () => {
            const response = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/api/order/seller`, {
                withCredentials: true,
            })
            const data = await response.data
            setOrderList(data.orderList)
        }
        fetchDataProduct()
    }, []);

    const onUpdate = async (order: IOrderResponse) => {
        let body = ""
        if (order.status === "PENDING") {
            body = JSON.stringify(EPayment.CONFIRMED)
        } else if (order.status === "CONFIRMED") {
            body = JSON.stringify(EPayment.SHIPPED)
        } else if (order.status === "SHIPPED") {
            body = JSON.stringify(EPayment.DELIVERED)
        }
        const response = await axios.put(`${process.env.NEXT_PUBLIC_API_URL}/api/order/update-order-status/${order.id}`,body, {
            withCredentials: true
        })
    }
    const columns = getColumns({onUpdate})

    return (
        <DataTable columns={columns} data={orderList}/>
    )
}

export default SellerClient