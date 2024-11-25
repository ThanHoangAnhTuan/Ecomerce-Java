"use client"

import Image from "next/image";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import React, {useEffect, useState} from "react";
import {EPayment, IOrderRequest, IProduct} from "@/app/types/types";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";
import {Separator} from "@/components/ui/separator";
import axios from "axios";
import {useToast} from "@/hooks/use-toast";

const ProductDetailClient = ({product}: { product: IProduct }) => {
    const [quantity, setQuantity] = useState<number>(1)
    const {toast} = useToast()

    useEffect(() => {
        if (quantity === 0) {
            setQuantity(1)
        }
    }, [quantity]);

    if (!product) {
        return (
            <></>
        )
    }

    const handleBuyProduct = async () => {
        try {
            const order: IOrderRequest = {
                orderItemList: [{
                    productId: product.id,
                    quantity,
                }],
                paymentInfo: {
                    amount: 0,
                    method: "cash",
                    status: EPayment.PENDING,
                }
            }
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
        <>
            <div className={"grid grid-cols-7 px-20 pt-5 bg-[#f1f1f1] gap-5 h-screen"}>
                <div className={"col-span-2 bg-white rounded h-fit p-4"}>
                    <Image src={product?.image} alt={product?.image} width={100} height={100} unoptimized={true}
                           className={"w-full p-5 rounded border object-fill"}/>
                </div>
                <div className={"col-span-3 p-5 bg-white h-fit"}>
                    <h1 className={"text-2xl font-bold mb-5"}>{product?.name}</h1>
                    <h1 className={"text-red-500 font-bold text-xl "}>
                        {new Intl.NumberFormat('vi-VN').format(product?.price)} đ
                    </h1>
                </div>
                <div className={"col-span-2 bg-white h-fit p-5"}>
                    <h1 className={"flex items-center mb-5"}>
                        <Avatar>
                            <AvatarImage src={product?.user.image} alt={product?.user.name}/>
                            <AvatarFallback>
                                {product?.user.name.split(" ").map(word => word[0]).join("")}
                            </AvatarFallback>
                        </Avatar>
                        {product?.user.name}</h1>
                    <Separator className="my-4"/>
                    <div>
                        <h1 className={"font-bold"}>Số lượng</h1>
                        <div className={"flex gap-2 w-[128px] my-5"}>
                            <Button disabled={quantity === 1} onClick={() => setQuantity(quantity - 1)}
                                    className={"bg-white text-black hover:bg-[#f1f1f1] flex-1"}>-</Button>
                            <Input className={"flex-1"} min={1} type={"number"} value={quantity}
                                   onChange={(e) => setQuantity(Number(e.target.value))}/>
                            <Button onClick={() => setQuantity(quantity + 1)}
                                    className={"bg-white text-black hover:bg-[#f1f1f1] flex-1"}>+</Button>
                        </div>
                    </div>
                    <h1 className={"font-bold"}>
                        Tạm tính
                    </h1>
                    <h1 className={"text-2xl font-bold my-2"}>{new Intl.NumberFormat('vi-VN', {
                        style: 'currency',
                        currency: 'VND',
                    }).format(product?.price * quantity)}</h1>
                    <Button className={"w-full bg-red-500 hover:bg-red-400 my-2 shadow-2xl"}
                            onClick={() => handleBuyProduct()}>Mua ngay</Button>
                    <Button className={"w-full bg-white text-black hover:bg-white"}>Thêm vào giỏ hàng</Button>
                </div>
            </div>
        </>
    )
}

export default ProductDetailClient;