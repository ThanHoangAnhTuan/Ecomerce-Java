"use client"

import {DataTable} from "@/app/(buyer)/my-buyer-order/components/data-table";
import * as React from "react";
import {getColumns} from "@/app/(buyer)/my-buyer-order/components/columns";
import {IOrderResponse} from "@/app/types/types";
import {useEffect, useState} from "react";
import axios from "axios";
import {useToast} from "@/hooks/use-toast";

const BuyerClient = () => {
    const [orderList, setOrderList] = useState<IOrderResponse[]>([])
    const {toast} = useToast()

    const fetchDataProduct = async () => {
        const response = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/api/order/buyer`, {
            withCredentials: true,
        })
        const data = await response.data
        setOrderList(data.orderList)
    }

    useEffect(() => {
        fetchDataProduct()
    }, []);

    const onUpdate = async (order: IOrderResponse) => {
        try {
            const response = await axios.put(`${process.env.NEXT_PUBLIC_API_URL}/api/order/cancel-order/${order.id}`, null, {
                withCredentials: true,
            })
            const data = await response.data
            toast({
                className: "bg-green-500 text-white",
                title: "Success",
                description: data.message
            })
            await fetchDataProduct()
        } catch (e:unknown) {
            toast({
                className: "bg-red-500 text-white",
                title: "Error",
                description: (e as Error).message
            })
        }
    }
    const columns = getColumns({onUpdate})

    return (
        <DataTable columns={columns} data={orderList}/>
    )
}

export default BuyerClient