"use client"

import Image from "next/image";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import React, {useEffect, useState} from "react";
import {EPayment, IOrderRequest, IProductInCart} from "@/app/types/types";
import {HiOutlineTrash} from "react-icons/hi";
import {Checkbox} from "@/components/ui/checkbox";
import {CheckedState} from "@radix-ui/react-checkbox";
import {useRouter} from "next/navigation";
import axios from "axios";
import {useToast} from "@/hooks/use-toast";
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
} from "@/components/ui/alert-dialog"

const OrderDetailsClientPage = () => {

    const [data, setData] = useState<IProductInCart[]>([])
    const router = useRouter()
    const {toast} = useToast()
    const [openAlertDialog, setOpenAlertDialog] = useState(false)

    useEffect(() => {
        const cartData = localStorage.getItem("cart");
        const cart: IProductInCart[] = cartData ? JSON.parse(cartData) : [];
        setData(cart)
    }, []);

    const handleUpdateQuantity = (id: number, newQuantity: number) => {
        const newData = data.map((item) => {
            if (item.id === id) {
                return {
                    ...item, quantity: newQuantity
                }
            } else {
                return item;
            }
        })
        localStorage.setItem("cart", JSON.stringify(newData))
        setData(newData)
    }

    const handleChangeCheckbox = (id: number, checked: CheckedState) => {
        const isChecked: boolean = checked === true
        const newData = data.map((item) => {
            if (item.id === id) {
                return {
                    ...item, checkbox: isChecked
                }
            } else {
                return item;
            }
        })
        localStorage.setItem("cart", JSON.stringify(newData))
        setData(newData)
    }

    const handleBuyProduct = async () => {
        const product = data.filter((item) => item.checkbox)
        if (product.length === 0) {
            setOpenAlertDialog(true)
            return
        }
        try {
            const order: IOrderRequest = {
                orderItemList: product.map(({id, quantity}) => ({productId: id, quantity: quantity})),
                paymentInfo: {
                    amount: 0,
                    method: "cash",
                    status: EPayment.PENDING,
                }
            }
            console.log()
            const response = await axios.post(`${process.env.NEXT_PUBLIC_API_URL}/api/order/buy-product`,
                order, {
                    withCredentials: true,
                })
            const data = await response.data;
            toast({
                className: "bg-green-500 text-white",
                title: "Error",
                description: data.message
            })
        } catch (e: unknown) {
            toast({
                className: "bg-red-500 text-white",
                title: "Error",
                description: (e as Error).message
            })
        }
    }

    return (
        <div className={"bg-[#f1f1f1] h-screen pt-4"}>
            {
                data.length === 0 && (
                    <div className={"bg-white mx-40 p-10"}>
                        <h1 className={"font-bold text-3xl"}>Chưa có sản phẩm nào trong giỏ hàng</h1>
                        <Button className={"bg-red-500 hover:bg-red-400 mt-5"} onClick={() => router.push("/")}>Mua
                            hàng</Button>
                    </div>
                )
            }
            {
                data.length !== 0 && (
                    <div className={"flex justify-center mx-40 rounded gap-5"}>
                        <div className={"flex-[2] bg-white"}>
                            {
                                data.map((item) => (
                                    <div key={item.id} className={"flex items-center p-4"}>
                                        <Checkbox checked={item.checkbox}
                                                  onCheckedChange={(checked: CheckedState) => handleChangeCheckbox(item.id, checked)}/>
                                        <div className={"flex items-center ml-4"}>
                                            <div className={"p-2 border rounded"}>
                                                <Image src={item.image} alt={item.name} width={50} height={50}/>
                                            </div>
                                            <h1 className={"px-5"}>{item.name}</h1>
                                        </div>
                                        <div className={"flex items-center ml-auto"}>
                                            <h1 className={"text-red-500 font-bold mr-4"}>{new Intl.NumberFormat('vi-VN').format(item.price)} đ</h1>
                                            <div className={"flex gap-2 w-[128px] my-5"}>
                                                <Button disabled={item.quantity === 1}
                                                        onClick={() => handleUpdateQuantity(item.id, item.quantity - 1)}
                                                        className={"bg-white text-black hover:bg-[#f1f1f1] flex-1"}>-</Button>
                                                <Input className={"flex-1"} min={1} type={"number"} value={item.quantity}
                                                       onChange={(e) => handleUpdateQuantity(item.id, Number(e.target.value))}/>
                                                <Button onClick={() => handleUpdateQuantity(item.id, item.quantity + 1)}
                                                        className={"bg-white text-black hover:bg-[#f1f1f1] flex-1"}>+</Button>
                                            </div>
                                            <Button className={"bg-white hover:bg-white text-black shadow-none"}>
                                                <HiOutlineTrash/>
                                            </Button>
                                        </div>
                                    </div>
                                ))
                            }
                        </div>
                        <div className={"flex-1 bg-white p-4 rounded"}>
                            <h1>Thông tin đơn hàng</h1>
                            <div className={"flex justify-between"}>
                                <h1>Tổng tiền: </h1>
                                <h1 className={"font-bold text-xl"}>
                                    {new Intl.NumberFormat('vi-VN').format(
                                        data.filter(product => product.checkbox)
                                            .reduce((total, product) =>
                                                total + product.price * product.quantity, 0))}
                                    đ
                                </h1>
                            </div>
                            <Button className={"bg-red-500 hover:bg-red-400 text-white w-full text-xl py-6"}
                                    onClick={() => handleBuyProduct()}>
                                Xác nhận đơn
                            </Button>
                        </div>
                    </div>
                )
            }
            <AlertDialog open={openAlertDialog} onOpenChange={setOpenAlertDialog}>
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>Lưu ý</AlertDialogTitle>
                        <AlertDialogDescription>
                            Bạn chưa chọn sản phẩm để mua
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogAction className={"bg-red-500 hover:bg-red-400 w-full"}>Đã hiểu</AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </div>
    )
}
export default OrderDetailsClientPage