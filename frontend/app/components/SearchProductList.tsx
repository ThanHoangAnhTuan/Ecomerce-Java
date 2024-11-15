"use client"

import Product from "@/app/components/Product";
import {IProduct} from "@/app/types/types";

const ProductList = ({productList}: { productList: IProduct[] }) => {

    return (
        <div className={"ml-5 grid grid-cols-4 w-full gap-4 h-fit"}>
            {
                productList?.map((product: IProduct) => (
                    <Product key={product.id} product={product}></Product>
                ))
            }
        </div>
    )
}

export default ProductList