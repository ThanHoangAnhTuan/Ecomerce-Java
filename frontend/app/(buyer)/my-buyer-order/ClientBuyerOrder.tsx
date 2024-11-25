"use client"

import {DataTable} from "@/app/(buyer)/my-buyer-order/components/data-table";
import * as React from "react";
import {getColumns} from "@/app/(buyer)/my-buyer-order/components/columns";
import {IOrderResponse} from "@/app/types/types";
import {useEffect, useState} from "react";
import axios from "axios";

const BuyerClient = () => {
    const [orderList, setOrderList] = useState<IOrderResponse[]>([])

    useEffect(() => {
        const fetchDataProduct = async () => {
            const response = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/api/order/buyer`, {
                withCredentials: true,
            })
            const data = await response.data
            setOrderList(data.orderList)
        }
        fetchDataProduct()
    }, []);

    const onUpdate = () => {
        console.log("On update")
    }
    const columns = getColumns({onUpdate})

    return (
        <DataTable columns={columns} data={orderList}/>
    )
}

export default BuyerClient