import React from "react";
import ProductDetailClient from "@/app/products/[...slug]/ProductDetailClient";
import HeaderServer from "@/app/components/HeaderServer";
import {ENV} from "@/config/config";

const ProductDetailServer = async ({params}: { params: Promise<{ slug: string }> }) => {
    let product = null
    try {
        const slug = (await params).slug[1]
        const response = await fetch(`${ENV.SERVER_API_URL}/api/products/get-product-by-id/${slug}`)
        const data = await response.json();
        product = data.product;
    } catch (e: unknown) {
        console.log((e as Error))
    }
    return (
        <>
            <HeaderServer></HeaderServer>
            <ProductDetailClient product={product}></ProductDetailClient>
        </>
    )
}

export default ProductDetailServer