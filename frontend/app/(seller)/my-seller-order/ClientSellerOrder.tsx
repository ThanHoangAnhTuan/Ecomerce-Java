"use client"

import {DataTable} from "@/app/(seller)/my-seller-order/components/data-table";
import * as React from "react";
import {getColumns} from "@/app/(seller)/my-seller-order/components/columns";
import {EPayment, IOrderResponse} from "@/app/types/types";
import {useEffect, useState} from "react";
import axios from "axios";
import {useToast} from "@/hooks/use-toast";

const SellerClient = () => {
    const [orderList, setOrderList] = useState<IOrderResponse[]>([])
    const {toast} = useToast()
    const fetchDataProduct = async () => {
        const response = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/api/order/seller`, {
            withCredentials: true,
        })
        const data = await response.data
        console.log(data)
        setOrderList(data.orderList)
    }

    useEffect(() => {

        fetchDataProduct()
    }, []);

    const onUpdate = async (order: IOrderResponse) => {
        const response = await axios.put(`${process.env.NEXT_PUBLIC_API_URL}/api/order/update-order-status/${order.id}`,null, {
            withCredentials: true
        })
        const data = await response.data;
        toast({
            className: "bg-red-500 text-white",
            title: "Error",
            description: data.message
        })
        await fetchDataProduct()
    }
    const columns = getColumns({onUpdate})

    return (
        <DataTable columns={columns} data={orderList}/>
    )
}

export default SellerClient