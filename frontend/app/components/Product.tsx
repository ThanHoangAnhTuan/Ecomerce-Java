import Image from "next/image";
import {Rating} from "@mui/material";
import Link from "next/link";
import {removeVietnameseTonesAndReplaceSpaces} from "@/app/utils/removeVietnameseTonesAndReplaceSpaces";
import {IProduct} from "@/app/types/types";

const Product = ({product}: {product: IProduct}) => {
    if (!product) {
        return <></>
    }
    return (
        <Link href={`/products/${removeVietnameseTonesAndReplaceSpaces(product?.name)}/${product?.id}`} className={"bg-white h-[500px]"}>
            <Image src={product?.image} alt={product?.name} width={"100"} height={"100"} className={"w-full"}/>
            <div className={"px-3"}>
                <h1 className={"text-red-500 font-bold text-xl "}>
                    {new Intl.NumberFormat('vi-VN').format(product?.price)} đ
                </h1>
                <h1 className={"line-clamp-3 my-3"}>{product?.name}</h1>
                <div className={"flex items-center"}>
                    <Rating name="read-only" value={5} readOnly size="small"/>
                    <h1 className={"ml-2"}>Đã bán: 1</h1>
                </div>
            </div>
        </Link>
    )
}

export default Product
