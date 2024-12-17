"use client"

import Image from "next/image";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import React, {useEffect, useState} from "react";
import {IProduct, IProductInCart} from "@/app/types/types";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";
import {Separator} from "@/components/ui/separator";
import {useRouter} from "next/navigation";
import {z} from "zod"
import {zodResolver} from "@hookform/resolvers/zod"
import {useForm} from "react-hook-form"
import {
    Form,
    FormControl,
    FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form"

const formSchema = z.object({
    comment: z.string().min(1)
})

const ProductDetailClient = ({product}: { product: IProduct }) => {
    const [quantity, setQuantity] = useState<number>(1)
    const router = useRouter()

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            comment: "",
        },
    })

    useEffect(() => {
        if (quantity === 0) {
            setQuantity(1)
        }
    }, [quantity]);

    if (!product) {
        return null
    }

    const handleAddProductToLocalStorage = async () => {
        const cartData = localStorage.getItem("cart");
        const cart = cartData ? JSON.parse(cartData) : [];
        const foundProduct: IProductInCart = cart.find((item: IProductInCart) => item.id === product.id)
        if (foundProduct) {
            foundProduct.quantity += quantity;
        } else {
            const productInCart = {
                id: product.id,
                image: product.image,
                name: product.name,
                price: product.price,
                quantity: quantity,
                checkbox: true
            }
            cart.push(productInCart)
        }
        localStorage.setItem("cart", JSON.stringify(cart))
    }

    const handleBuyProduct = async () => {
        await handleAddProductToLocalStorage()
        router.push("/order-details")
    }

    const addProductToCart = async () => {
        await handleAddProductToLocalStorage()
        // show message
    }

    // 2. Define a submit handler.
    function onSubmit(values: z.infer<typeof formSchema>) {
        // Do something with the form values.
        // ✅ This will be type-safe and validated.
        console.log(values)
    }

    return (
        <div className={"flex flex-col "}>
            <div className={"grid grid-cols-7 px-20 pt-5 bg-[#f1f1f1] gap-5"}>
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
                    <Button className={"w-full bg-white text-black hover:bg-white"} onClick={() => addProductToCart()}>Thêm
                        vào giỏ hàng</Button>
                </div>
            </div>
            <div className={"bg-[#f1f1f1] pt-5"}>
                <div className={"mx-20 bg-white px-4"}>
                    <h1 className={"text-2xl"}>Khách hàng nói về sản phẩm</h1>
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="flex gap-2">
                            <FormField
                                control={form.control}
                                name="comment"
                                render={({field}) => (
                                    <FormItem className={"flex-1"}>
                                        <FormControl>
                                            <Input placeholder={"Nhập nội dung bình luận"} {...field}/>
                                        </FormControl>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <Button type="submit">Gửi bình luận</Button>
                        </form>
                    </Form>
                    <div>
                        <h1>Chưa có bình luận nào</h1>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default ProductDetailClient;