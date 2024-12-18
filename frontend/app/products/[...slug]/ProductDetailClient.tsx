"use client"

import Image from "next/image";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import React, {useEffect, useState} from "react";
import {IProduct, IProductInCart, IUser} from "@/app/types/types";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";
import {Separator} from "@/components/ui/separator";
import {useRouter} from "next/navigation";
import {z} from "zod"
import {zodResolver} from "@hookform/resolvers/zod"
import {useForm} from "react-hook-form"
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormMessage,
} from "@/components/ui/form"
import axios from "axios";
import {Rating} from "@mui/material";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import {Label} from "@/components/ui/label"

const formSchema = z.object({
    comment: z.string().min(1, {message: "Vui lòng nhập nội dung bình luận!"})
})

interface IReview {
    id: number,
    content: string,
    createAt: string,
    product: IProduct,
    rating: number,
    user: IUser,
}

const ProductDetailClient = ({product}: { product: IProduct }) => {
    const [quantity, setQuantity] = useState<number>(1)
    const router = useRouter()
    const [commentList, setCommentList] = useState<IReview[]>([]);
    const [valueRating, setValueRating] = useState<number | null>(1);
    const [open, setOpen] = useState<boolean>(false)
    const [commentContent, setCommentContent] = useState<string>("");

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
        router.push("/")
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

    useEffect(() => {
        try {
            const fetchReview = async () => {
                const response = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/api/review/get-all-review-by-product-id/${product.id}`)
                const data = await response.data;
                console.log(data)
                setCommentList(data.reviewList)
            }
            fetchReview()
        } catch (e: unknown) {
            console.log((e as Error).message)
        }
    }, [product]);

    function onSubmit(values: z.infer<typeof formSchema>) {
        setOpen(true);
        setCommentContent(values.comment);
    }

    const handleAddReview = async () => {
        try {
            const response = await axios.post(`${process.env.NEXT_PUBLIC_API_URL}/api/review/add-review`, {
                content: commentContent,
                productId: product.id,
                rating: valueRating,
            }, {
                withCredentials: true
            })
            const data = await response.data;
            setCommentList([
                data.review,
                ...commentList,
            ])
            setOpen(false);
            form.reset();
            setValueRating(1);
        } catch (e: unknown) {
            console.log((e as Error).message)
        }
    }

    console.log(commentList)

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
                        {
                            commentList?.map((comment) => {
                                const [year, month, day] = comment.createAt.split("T")[0].split("-");
                                const formattedDate = `${day}/${month}/${year}`;
                                return (
                                    <div key={comment.id} className={"mt-5"}>
                                        <div className={"flex gap-5"}>
                                            <Avatar>
                                                <AvatarImage src={comment.user.image}/>
                                                <AvatarFallback>{comment.user.name.charAt(0)}</AvatarFallback>
                                            </Avatar>
                                            <div>
                                                <h1>{comment.content}</h1>
                                                <p>{formattedDate}</p>
                                                <Rating name="read-only" value={comment.rating} readOnly/>
                                            </div>
                                        </div>
                                    </div>
                                )
                            })
                        }
                        {
                            commentList.length === 0 && (
                                <h1>Chưa có bình luận nào</h1>
                            )
                        }
                    </div>
                </div>
            </div>

            <Dialog open={open} onOpenChange={setOpen}>
                <DialogContent className="sm:max-w-[425px]">
                    <Rating
                        name="simple-controlled"
                        value={valueRating}
                        onChange={(event, newValue) => {
                            setValueRating(newValue);
                        }}
                    />
                    <DialogFooter>
                        <Button type={"button"} onClick={() => handleAddReview()}>Save changes</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}

export default ProductDetailClient;