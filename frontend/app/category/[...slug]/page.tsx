import Product from "@/app/components/Product";
import {IProduct} from "@/app/types/types"
import HeaderServer from "@/app/components/HeaderServer";
import React from "react";


const CategoryDetails = async ({params}: { params: Promise<{ slug: string }> }) => {
    const slug = (await params).slug[1]
    let productList = null
    console.log(process.env.SERVER_API_URL)
    console.log(process.env.NEXT_PUBLIC_API_URL)
    const response = await fetch(`${process.env.SERVER_API_URL}/api/products/get-product-by-category-id/${slug}`)
    if (response.ok) {
        const data = await response.json();
        productList = data.productList;
    } else {
        console.error('Error fetching product:', response.status);
    }
    return(
        <>
            <HeaderServer></HeaderServer>
            <div className={"pl-5 pt-5 grid grid-cols-6 w-full gap-4 bg-[#f1f1f1] h-screen"}>
                {
                    productList?.map((product: IProduct) => (
                        <Product key={product.id} product={product}></Product>
                    ))
                }
            </div>
        </>
    )
}

export default CategoryDetails