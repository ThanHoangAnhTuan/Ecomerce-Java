"use client"

import {useEffect, useState} from "react";
import Product from "@/app/components/Product";
import {IProduct} from "@/app/types/types";
import {ENV} from "@/config/config";
import axios from "axios";

const ProductList = () => {
    const [productList, setProductList] = useState([])

    useEffect(() => {
        const fetchDataProduct = async () => {
            const response = await axios.get(`${ENV.CLIENT_API_URL}/api/products/get-all-products`, {
                withCredentials: true
            })
            const data = await response.data
            setProductList(data.productList)
        }
        fetchDataProduct()
    }, []);

    return (
        <div className={"ml-5 grid grid-cols-4 w-full gap-4 h-fit"}>
            {
                productList?.map((product:IProduct) => (
                    <Product key={product.id} product={product}></Product>
                ))
            }
        </div>
    )
}

export default ProductList