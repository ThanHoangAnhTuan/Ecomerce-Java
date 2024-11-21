"use client"

import {useSearchParams} from "next/navigation";
import {useEffect, useState} from "react";
import axios from "axios";
import Menu from "@/app/components/Menu";
import SearchProductList from "@/app/components/SearchProductList";
import {IProduct} from "@/app/types/types";

const SearchPage = () => {
    const searchParams = useSearchParams();
    const keyword = searchParams.get("keyword")
    const [productList, setProductList] = useState<IProduct[]>([])

    useEffect(() => {
        const fetchDataSearchProduct = async () => {
            try {
                const response = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/api/products/search-product?keyword=${keyword}`, {
                    withCredentials: true
                })
                const data = await response.data;
                setProductList(data.productList)
            } catch (e: unknown) {
                console.log((e as Error).message)
            }
        }
        fetchDataSearchProduct()
    }, [keyword]);

    return (
        <>
            <main className={"bg-[#f1f1f1] flex px-20 py-5"}>
                <Menu></Menu>
                <SearchProductList productList={productList}></SearchProductList>
            </main>
        </>
    )
}

export default SearchPage